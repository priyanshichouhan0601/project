package models

import play.api.libs.json.{Json, Reads}

case class GuestInfo(name: String, email: String)

object GuestInfo {
  implicit val guestInfoReads: Reads[GuestInfo] = Json.reads[GuestInfo]
}
