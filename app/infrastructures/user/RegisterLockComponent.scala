package infrastructures.user

trait RegisterLockComponent {

  def lock(key: String): Either[IllegalStateException, Unit]

  def unlock(key: String): Unit

}
