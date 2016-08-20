package infrastructures.point

import javax.inject.Inject

import services.user.PointRepository

import scala.concurrent.{ ExecutionContext, Future }

class PointDao @Inject()(ec: ExecutionContext) extends PointRepository {
  override def append(point: Int): Future[Int] = Future.successful(100)
}
