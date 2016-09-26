package services.user

import scala.concurrent.Future

trait UserRepository {

  def findByName(name: String): Future[Option[String]]

  def register(id: String, name: String): Future[String]

}
