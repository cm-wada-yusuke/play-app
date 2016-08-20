package infrastructures.user

import javax.inject.Inject

import services.user.UserRepository

import scala.concurrent.{ ExecutionContext, Future }

class UserDao @Inject()(ec: ExecutionContext) extends UserRepository {

  override def register(id: String, name: String): Future[String] = Future.successful("fasf12dasv")

}
