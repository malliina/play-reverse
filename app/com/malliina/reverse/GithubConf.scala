package com.malliina.reverse

import com.malliina.http.FullUrl
import com.malliina.values.ErrorMessage
import play.api.{Configuration, Mode}

case class GithubConf(githubSecret: String, jenkinsUnixUrl: FullUrl, jenkinsWindowsUrl: FullUrl)

object GithubConf {
  val SecretKey = "reverse.github.secret"
  val UnixKey = "reverse.jenkins.unix.url"
  val WindowsKey = "reverse.jenkins.windows.url"

  def forMode(mode: Mode, conf: Configuration): GithubConf = {
    if (mode == Mode.Prod) GithubConf.fromConfOrFail(conf)
    else if (mode == Mode.Test) fake
    else GithubConf.fromEnv().getOrElse(fake)
  }

  def fake = {
    val dest = FullUrl.build("http://www.google.com").toOption.get
    GithubConf("", dest, dest)
  }

  def fromEnvOrFail(): GithubConf =
    fromEnv().fold(e => throw new Exception(e.message), identity)

  def fromEnv() = build(key => sys.env.get(key).orElse(sys.props.get(key)))

  def fromConfOrFail(conf: Configuration) =
    fromConf(conf).fold(err => throw new Exception(err.message), identity)

  def fromConf(conf: Configuration) = build(key => conf.getOptional[String](key))

  def build(readKey: String => Option[String]) = {
    def read(key: String) = readKey(key).toRight(ErrorMessage(s"Key missing: '$key'."))

    for {
      secret <- read(SecretKey)
      unixStr <- read(UnixKey)
      unixUrl <- FullUrl.build(unixStr)
      windowsStr <- read(WindowsKey)
      windowsUrl <- FullUrl.build(windowsStr)
    } yield GithubConf(secret, unixUrl, windowsUrl)
  }
}
