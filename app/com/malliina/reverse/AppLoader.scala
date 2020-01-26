package com.malliina.reverse

import com.malliina.play.app.DefaultApp
import controllers.{AssetsComponents, JenkinsProxy}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.hosts.AllowedHostsConfig
import router.Routes

class AppLoader extends DefaultApp(new AppComponents(_))

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AhcWSComponents
  with AssetsComponents {
  val conf = GithubConf.forMode(environment.mode, configuration)
  val home = new JenkinsProxy(conf, wsClient, controllerComponents)
  override lazy val allowedHostsConfig = AllowedHostsConfig(Seq("ci.malliina.com", "localhost"))
  override val router: Router = new Routes(httpErrorHandler, home)
}
