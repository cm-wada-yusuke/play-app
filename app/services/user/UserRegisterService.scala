package services.user

import java.util.UUID
import javax.inject.Inject

import core.util.FutureSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRegisterService @Inject()(
    lockComponent: RegisterLockComponent,
    userRepository: UserRepository,
    pointRepository: PointRepository
) {

  def register(userName: String): Future[(String, Int)] = {
    val userId = UUID.randomUUID().toString
    val bonusPoint = 100
    val result = for {
      _ <- FutureSupport.eitherToFuture(lockComponent.lock(userId))
      sessionId <- userRepository.register(userId, userName)
      userPoint <- pointRepository.append(bonusPoint)
    } yield (sessionId, userPoint)
    result.onComplete(_ => lockComponent.unlock(userId))
    result
  }
}
