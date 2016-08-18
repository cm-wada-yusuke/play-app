import java.time.Clock
import javax.inject.Named

import com.google.inject.{ AbstractModule, Provides, Singleton }
import com.redis.RedisClientPool
import play.api.Configuration
import services.{ ApplicationTimer, AtomicCounter, Counter }

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])
  }

  @Provides
  @Singleton
  @Named("register_lock")
  def provideLockRedisClientPool(configuration: Configuration): RedisClientPool =
    generateRedisClientPool(configuration, configuration.getInt("redis.dbNumber.lock").get)

  private def generateRedisClientPool(configuration: Configuration, databaseNumber: Int): RedisClientPool =
    new RedisClientPool(
      configuration.getString("redis.host").get,
      configuration.getInt("redis.port").get,
      database = databaseNumber
    )
}

