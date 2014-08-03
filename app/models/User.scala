package models

import play.api.db.slick._
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._

/**
 * Created by lucho on 03/08/14.
 */

case class User(id: Option[Long], name: String, passwordHash: String)

class Users(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def passwordHash = column[String]("password_hash")
  def * = (id.?, name, passwordHash) <> (User.tupled, User.unapply)
}

object Users extends TableQuery(new Users(_)) {

  class EncryptableString(s: String) { // pimping String

    def encryptionHash = "encrypted(" + s + ")"
  }

  implicit def encryptableString(s: String) = new EncryptableString(s)

  def correctCredentials(name: String, password: String): Boolean = DB.withSession { implicit session =>
    val passwordHash = password.encryptionHash
    this.filter(u => u.name === name && u.passwordHash === passwordHash).length.run == 0
  }

  def register(name: String, password: String) = DB.withSession { implicit session =>
    this += User(None, name, password.encryptionHash)
  }

  def findOneByUsername(name: String): Option[User] = DB.withSession { implicit session =>
    this.filter(u => u.name === name).firstOption
  }
}
