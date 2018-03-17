package com.malliina.app

import com.malliina.play.app.DefaultApp
import com.malliina.reverse.GithubConf
import controllers.{AssetsComponents, GithubProxy}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes

class AppLoader extends DefaultApp(new AppComponents(_))

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with AhcWSComponents
    with AssetsComponents {
  val home = new GithubProxy(GithubConf.fromEnvOrFail(), wsClient, controllerComponents)
  override val router: Router = new Routes(httpErrorHandler, home)
}
