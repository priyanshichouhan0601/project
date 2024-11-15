package models

import spray.json.DefaultJsonProtocol._
import spray.json._

case class GuestInfo(name: String, email: String)

object JsonFormats {
  implicit val guestFormat: RootJsonFormat[GuestInfo] = jsonFormat2(GuestInfo)
}
