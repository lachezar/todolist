package models

import play.api.db.slick._
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._

/**
 * Created by lucho on 31/07/14.
 */
case class Task(id: Option[Long] = None, label: String)

class Tasks(tag: Tag) extends Table[Task](tag, "tasks") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def label = column[String]("label")
  def * = (id.?, label) <> (Task.tupled, Task.unapply)
}

object Tasks extends TableQuery(new Tasks(_)) {

  def all(): List[Task] = DB.withSession { implicit session =>
    this.run.toList
  }

  def create(label: String) = DB.withSession { implicit session =>
    this += Task(None, label)
  }

  def delete(id: Long) = DB.withSession { implicit session =>
    this.filter(_.id === id).delete
  }
}