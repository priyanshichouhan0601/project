package controllers

import models.{Menu, MenuDAO}
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class MenuController @Inject()(menuDao: MenuDAO, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val menuFormat: Format[Menu] = Json.format[Menu]

  def insertMenu(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Seq[Menu]].fold(
      errors => Future.successful(BadRequest("Invalid JSON provided")),
      itemsToInsert => {
        menuDao.insertMenuItem(itemsToInsert).map {
          case Some(count) => Ok(Json.obj("message" -> s"Inserted $count items"))
          case None => Ok(Json.obj("message" -> "Menu items inserted successfully"))
        }.recover {
          case ex => InternalServerError(Json.obj("message" -> s"Failed to insert users: ${ex.getMessage}"))
        }
      }
    )
  }

  def fetchMenu() = Action.async {
    menuDao.list().map { people =>
      Ok(Json.toJson(people))
    }
  }

}
