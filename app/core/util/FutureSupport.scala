package core.util

import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsError, JsPath, JsResult, JsSuccess }

object FutureSupport {

  import scala.concurrent.Future

  /**
   * EitherをFutureに変換する
   *
   * @param r Either
   * @tparam A Leftの型
   * @tparam B Right, Futureの型
   * @return Future
   */
  def eitherToFuture[A <: Throwable, B](r: => Either[A, B]): Future[B] = r match {
    case Right(value) => Future.successful(value)
    case Left(e) => Future.failed(e)
  }

  /**
   * JsResultをFutureに変換する
   *
   * @param r JsResult
   * @tparam A JsResult, Futureの型
   * @return Future
   */
  def jsResultToFuture[A](r: => JsResult[A]): Future[A] = r match {
    case JsSuccess(res, _) => Future.successful(res)
    case JsError(error) => Future.failed(JsonValidationError(error))
  }

}

/**
 * Jsonのバリデーションエラーを表すデータ型
 *
 * @param errors エラー情報
 */
case class JsonValidationError(errors: Seq[(JsPath, Seq[ValidationError])]) extends RuntimeException
