package com.malliina.reverse

import com.malliina.http.FullUrl
import com.malliina.values.ErrorMessage

case class GithubConf(githubSecret: String,
                      jenkinsUnixUrl: FullUrl,
                      jenkinsWindowsUrl: FullUrl)

object GithubConf {
  val SecretKey = "github_secret"
  val UnixKey = "jenkins_unix_url"
  val WindowsKey = "jenkins_windows_url"

  def fromEnvOrFail(): GithubConf =
    fromEnv().fold(e => throw new Exception(e.message), identity)

  def fromEnv() = {
    for {
      secret <- read(SecretKey)
      unixStr <- read(UnixKey)
      unixUrl <- FullUrl.build(unixStr)
      windowsStr <- read(WindowsKey)
      windowsUrl <- FullUrl.build(windowsStr)
    } yield GithubConf(secret, unixUrl, windowsUrl)
  }

  def read(key: String) = sys.env.get(key).orElse(sys.props.get(key))
    .toRight(ErrorMessage(s"Key missing: '$key'."))
}
