package controllers

<#if playVersion == "2.7" || playVersion == "2.6">
import javax.inject._
import play.api._
import play.api.mvc._

import org.test.Util

@Singleton
class Application @Inject() extends InjectedController {

  def index = Action {
    Ok(Util.fullStop("Your new application is ready"))
  }

  def shutdown = Action {
    Runtime.getRuntime().halt(0)
    Ok("shutdown")
  }
}

<#else>
import play.api._
import play.api.mvc._

import org.test.Util

object Application extends Controller {

  def index = Action {
    Ok(Util.fullStop("Your new application is ready"))
  }

  def shutdown = Action {
    System.exit(0)
    Ok("shutdown")
  }
}

</#if>
