package controllers

import javax.inject._
import play.api.mvc._
import repositories.GuestRepository
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.Json
import play.api.Logging

@Singleton
class GuestController @Inject()(
                                 val controllerComponents: ControllerComponents,
                                 guestRepository: GuestRepository
                               )(implicit ec: ExecutionContext) extends BaseController with Logging {

  def getActiveGuests: Action[AnyContent] = Action.async {
    guestRepository.getActiveGuests.map { guests =>
      // Filter only the required fields (name and email) and convert to JSON
      val guestInfo = guests.map(guest => Json.obj("name" -> guest.name, "email" -> guest.email))
      Ok(Json.obj("activeGuests" -> guestInfo))
    } recover {
      case ex: Exception =>
        logger.error("Error retrieving active guests", ex)
        InternalServerError(Json.obj("message" -> "Failed to retrieve active guests"))
    }
  }
}
