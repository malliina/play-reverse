package com.malliina.reverse

import play.api.libs.json.Json

case class AppMeta(name: String, version: String, gitHash: String)

object AppMeta {
  implicit val json = Json.format[AppMeta]

  val default = AppMeta(BuildInfo.name, BuildInfo.version, BuildInfo.hash)
}
