package infrastructures.point

import javax.inject.Inject

import services.user.PointRepository

import scala.concurrent.{ ExecutionContext, Future }

class PointClinet @Inject()(implicit ec: ExecutionContext) extends PointRepository {
  override def append(point: Int): Future[Int] = Future {
    Thread.sleep(1000)
    100
  }
}
