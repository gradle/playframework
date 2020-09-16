package controllers.submodule

<#if playVersion == "2.7" || playVersion == "2.6">
import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class Application @Inject() extends InjectedController {

  def index = Action {
    Ok("Submodule page")
  }

}
<#else>
import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok("Submodule page")
  }

}

</#if>
