package controllers

import models.Tasks
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Application.tasks)
  }

  def tasks = Action {
    Ok(views.html.index(Tasks.all(), taskForm))
  }

  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(Tasks.all(), errors)),
      label => {
        Tasks.create(label)
        Redirect(routes.Application.tasks)
      }
    )

  }

  def deleteTask(id: Long) = Action.apply({
    Tasks.delete(id)
    Redirect(routes.Application.tasks)
  })

  val taskForm = Form("label" -> nonEmptyText)
}