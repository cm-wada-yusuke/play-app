package services.user

import java.util.UUID
import javax.inject.Inject

import core.util.FutureSupport
import domains.error.{ AlreadyRegisteredError, RegisterConflictError }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

class UserRegisterService @Inject()(
    userRepository: UserRepository,
    lockComponent: RegisterLockComponent,
    coinRepository: CoinRepository
) {

  val BonusCoin = 100

  def register(userName: String): Future[(String, Int)] = {
    val result = for {
      _ <- findUser(userName)
      _ <- FutureSupport.eitherToFuture(lock(userName))
      userId = UUID.randomUUID().toString
      sessionId <- userRepository.register(userId, userName)
      userCoin <- coinRepository.append(BonusCoin)
    } yield (sessionId, userCoin)

    result.onComplete {
      case Failure(_: RegisterConflictError | _: AlreadyRegisteredError) => ()
      case _ => lockComponent.unlock(userName)

    }
    result
  }

  private def findUser(userName: String): Future[Unit] =
    userRepository.findByName(userName).flatMap {
      case None => Future.successful(())
      case Some(id) => Future.failed(new AlreadyRegisteredError())
    }

  private def lock(userName: String): Either[RegisterConflictError, Unit] =
    lockComponent.lock(userName).left.map {
      _ => new RegisterConflictError()
    }
}
