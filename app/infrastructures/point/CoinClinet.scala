package infrastructures.point

import javax.inject.Inject

import services.user.CoinRepository

import scala.concurrent.{ ExecutionContext, Future }

class CoinClinet @Inject()(implicit ec: ExecutionContext) extends CoinRepository {
  override def append(point: Int): Future[Int] = Future {
    Thread.sleep(1000)
    100
  }
}
