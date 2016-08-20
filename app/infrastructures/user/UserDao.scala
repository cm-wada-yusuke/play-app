package infrastructures.user

import javax.inject.Inject

import services.user.UserRepository

import scala.concurrent.{ ExecutionContext, Future }

class UserDao @Inject()(implicit ec: ExecutionContext) extends UserRepository {

  override def register(id: String, name: String): Future[String] = Future {
    Thread.sleep(1000)
    "fasf12dasv"
  }

}
