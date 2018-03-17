package controllers

import com.malliina.reverse.GithubConf
import com.malliina.values.ErrorMessage
import org.apache.commons.codec.digest.HmacUtils
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class GithubProxy(conf: GithubConf, http: WSClient, comps: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(comps) {
  private val log = Logger(getClass)

  val SignatureHeader = "X-Hub-Signature"

  def proxied = Action(parse.tolerantText).async { req =>
    val result = for {
      signature <- req.headers.get(SignatureHeader).toRight(ErrorMessage(s"Header '$SignatureHeader' missing."))
      _ <- validateSignature(signature, req.body)
    } yield {
      val httpRequest = http.url(conf.githubUrl.append("/github-webhook/").url)
        .addHttpHeaders(req.headers.toSimpleMap.toSeq: _*)
        .post(req.body)
      httpRequest.map { r =>
        Status(r.status)(r.bodyAsBytes)
      }
    }
    result.fold(err => fut(fail(err)), identity)
  }

  def validateSignature(signature: String, payload: String) = {
    val digest = HmacUtils.hmacSha1Hex(conf.githubSecret, payload)
    val expected = s"sha1=$digest"
    if (expected == signature) Right(expected)
    else Left(ErrorMessage(s"Invalid signature: '$signature', expected '$expected'."))
  }

  def fail(err: ErrorMessage) = BadRequest(Json.obj("message" -> err))

  def unauth(err: ErrorMessage) = Unauthorized(Json.obj("message" -> err))

  def fut[T](t: T) = Future.successful(t)
}
