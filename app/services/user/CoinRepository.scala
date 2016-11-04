package services.user

import scala.concurrent.Future

trait CoinRepository {

  def append(point: Int): Future[Int]

}
