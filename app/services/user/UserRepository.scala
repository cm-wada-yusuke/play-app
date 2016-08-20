package services.user

import scala.concurrent.Future

trait UserRepository {

  def register(id: String, name: String): Future[String]

}
