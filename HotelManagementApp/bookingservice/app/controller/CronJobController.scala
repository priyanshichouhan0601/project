package controllers

import javax.inject._
import play.api.mvc._
import jobs.RoomStatusUpdateTask
import scala.concurrent.ExecutionContext

@Singleton
class CronJobController @Inject()(
                                   val controllerComponents: ControllerComponents,
                                   roomStatusUpdateTask: RoomStatusUpdateTask
                                 )(implicit ec: ExecutionContext) extends BaseController {

  def triggerCronJobManually: Action[AnyContent] = Action.async {
    roomStatusUpdateTask.checkAndUpdateRoomAndGuestStatus().map { _ =>
      Ok("Cron job triggered successfully.")
    }
  }
}
