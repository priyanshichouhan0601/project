package scheduler

import com.google.inject.AbstractModule
import play.api.Logging

class SchedulerModule extends AbstractModule with Logging {
  override def configure(): Unit = {
    logger.info("Starting Mail Scheduler")
    bind(classOf[RestaurantMailServiceScheduler]).asEagerSingleton()
  }
}