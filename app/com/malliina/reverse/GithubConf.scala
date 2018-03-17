package com.malliina.reverse

import com.malliina.http.FullUrl
import com.malliina.values.ErrorMessage

case class GithubConf(githubSecret: String, githubUrl: FullUrl)

object GithubConf {
  val SecretKey = "github_secret"
  val UrlKey = "github_url"

  def fromEnvOrFail(): GithubConf =
    fromEnv().fold(e => throw new Exception(e.message), identity)

  def fromEnv() = {
    for {
      secret <- read(SecretKey)
      urlStr <- read(UrlKey)
      url <- FullUrl.build(urlStr)
    } yield GithubConf(secret, url)
  }

  def read(key: String) = sys.env.get(key).orElse(sys.props.get(key))
    .toRight(ErrorMessage(s"Key missing: '$key'."))
}
