package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.data.FormFactory;
import javax.inject.*;
import model.*;

@Singleton
public class Application extends Controller {

  private FormFactory ff;

  @Inject
  public Application(FormFactory ff) {
    this.ff = ff;
  }

  public Result index() {

    Form<User> userForm = ff.form(User.class).bindFromRequest();

    return ok(views.html.indexUserForm.render(
            StringUtils.trim("   Your new application is ready.   "),
            userForm
    ));
  }

}
