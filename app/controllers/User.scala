package controllers

import models.Users
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

/**
 * Created by lucho on 03/08/14.
 */
object User extends Controller with Secured {

  def performRegistration = Action { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.registration(formWithErrors)),
      user => {
        Users.register(user.name, user.password)
        Redirect(routes.User.registrationSuccessful)
      }
    )
  }

  def registration = Action {
    Ok(views.html.registration(registrationForm))
  }

  def registrationSuccessful = Action {
    Ok("registration successful")
  }

  def login = Action {
    Ok(views.html.login(loginForm))
  }

  def performLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        Redirect(routes.User.loginSuccessful).withSession(Security.username -> user.name)
      }
    )
  }

  def loginSuccessful = withUser { user => implicit request =>
    Ok("you are logged in " + user.name)
  }

  val registrationForm = Form(
    mapping (
      "name" -> text(minLength = 1),
      "password" -> text(minLength = 1),
      "password_confirm" -> text
    )(RegistrationData.apply)(RegistrationData.unapply).verifying(
      // Add an additional constraint: both passwords must match
      "Passwords don't match", fields => fields match {
        case registrationData => {
          registrationData.password == registrationData.password_confirm
        }
      }
    )
  )

  case class RegistrationData(name: String, password: String, password_confirm: String)

  val loginForm = Form(
    mapping (
      "name" -> text(minLength = 1),
      "password" -> text(minLength = 1)
    )(LoginData.apply)(LoginData.unapply).verifying(
      // Add an additional constraint: both passwords must match
      "Incorrect name or passwords", fields => fields match {
        case loginData => {
          Users.correctCredentials(loginData.name, loginData.password)
        }
      }
    )
  )

  case class LoginData(name: String, password: String)

}

