package controllers

import com.malliina.http.FullUrl
import com.malliina.reverse.GithubConf
import com.malliina.values.ErrorMessage
import controllers.JenkinsProxy.log
import org.apache.commons.codec.digest.HmacUtils
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class JenkinsProxy(conf: GithubConf, http: WSClient, comps: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(comps) {

  val SignatureHeader = "X-Hub-Signature"

  def proxiedUnix = proxyTo(conf.jenkinsUnixUrl)

  def proxiedWindows = proxyTo(conf.jenkinsWindowsUrl)

  def proxyTo(url: FullUrl) = Action(parse.tolerantText).async { req =>
    val result = for {
      signature <- req.headers.get(SignatureHeader).toRight(ErrorMessage(s"Header '$SignatureHeader' missing."))
      _ <- validateSignature(signature, req.body)
    } yield {
      val httpRequest = http.url(url.append("/github-webhook/").url)
        .addHttpHeaders(req.headers.toSimpleMap.toSeq: _*)
        .post(req.body)
      httpRequest.map { r =>
        Status(r.status)(r.bodyAsBytes)
      }
    }
    result.fold(err => fut(logged(err, req)), identity)
  }

  private def validateSignature(signature: String, payload: String) = {
    val digest = HmacUtils.hmacSha1Hex(conf.githubSecret, payload)
    val expected = s"sha1=$digest"
    if (expected == signature) Right(expected)
    else Left(ErrorMessage(s"Invalid signature: '$signature', expected '$expected'."))
  }

  private def logged(err: ErrorMessage, req: RequestHeader) = {
    log.error(s"${err.message} from '$req'.")
    Unauthorized(Json.obj("message" -> err))
  }

  private def fut[T](t: T) = Future.successful(t)
}

object JenkinsProxy {
  private val log = Logger(getClass)
}
