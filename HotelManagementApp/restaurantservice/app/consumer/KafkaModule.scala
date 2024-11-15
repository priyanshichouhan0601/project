package consumer

import com.google.inject.AbstractModule
import play.api.Logging

class KafkaModule extends AbstractModule with Logging {

  override def configure(): Unit = {
    logger.info("Starting KafkaModule")
    bind(classOf[RestaurantKafkaConsumer]).asEagerSingleton()
  }
}
