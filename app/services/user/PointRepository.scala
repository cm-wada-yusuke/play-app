package services.user

import scala.concurrent.Future

trait PointRepository {

  def append(point: Int): Future[Int]

}
