/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

<#if playVersion == "2.8" || playVersion == "2.7" || playVersion == "2.6">
import javax.inject._
import com.google.common.base.Strings
import play.api._
import play.api.mvc._

@Singleton
class Application @Inject() extends InjectedController {

  def index = Action {
    Ok(views.html.index(Strings.nullToEmpty("Your new application is ready.")))
  }

  def shutdown = Action {
    Runtime.getRuntime().halt(0)
    Ok("shutdown")
  }
}
<#else>
import com.google.common.base.Strings
import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(Strings.nullToEmpty("Your new application is ready.")))
  }

  def shutdown = Action {
    System.exit(0)
    Ok("shutdown")
  }
}
</#if>
