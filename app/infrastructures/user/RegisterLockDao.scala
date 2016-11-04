package infrastructures.user

import javax.inject.{ Inject, Named }

import com.redis.RedisClientPool
import infrastructures.common.RedisJsonAdapter
import infrastructures.user.config.RegisterLockDaoConfig
import play.api.libs.json._
import services.user.RegisterLockComponent


class RegisterLockDao @Inject()(
    config: RegisterLockDaoConfig,
    @Named("register_lock") override val pool: RedisClientPool
) extends RedisJsonAdapter[Unit] with RegisterLockComponent {

  /**
   * Jsonと値のマッピングのためのフォーマッタ
   */
  override implicit def format: Format[Unit] = new Format[Unit] {
    override def writes(o: Unit): JsValue = Json.obj()

    override def reads(json: JsValue): JsResult[Unit] = JsSuccess(())
  }

  /**
   * ロックする。
   *
   * @return ロック失敗時、IllegalStateExceptionを返す。
   */
  override def lock(key: String): Either[IllegalStateException, Unit] =
  insertIfNotExists(key, config.ttl, ()) match {
    case true => Right(Unit)
    case false => Left(new IllegalStateException)
  }

  /**
   * アンロックする。
   *
   * @param key アンロックする。
   */
  override def unlock(key: String): Unit = deleteByKey(key)

}


