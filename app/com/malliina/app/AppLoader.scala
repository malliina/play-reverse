package com.malliina.app

import com.malliina.http.FullUrl
import com.malliina.play.app.DefaultApp
import com.malliina.reverse.GithubConf
import controllers.{AssetsComponents, GithubProxy}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{BuiltInComponentsFromContext, Mode}
import play.filters.HttpFiltersComponents
import play.filters.hosts.AllowedHostsConfig
import router.Routes

class AppLoader extends DefaultApp(new AppComponents(_))

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AhcWSComponents
    with AssetsComponents {
  val conf =
    if (environment.mode == Mode.Test) GithubConf("", FullUrl.build("http://www.google.com").toOption.get)
    else GithubConf.fromEnvOrFail()
  val home = new GithubProxy(conf, wsClient, controllerComponents)
  override lazy val allowedHostsConfig = AllowedHostsConfig(Seq("ci.malliina.com", "localhost"))
  override val router: Router = new Routes(httpErrorHandler, home)
}
