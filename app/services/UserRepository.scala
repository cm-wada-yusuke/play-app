package services

trait UserRepository {

  def register(name: String): Unit

}
