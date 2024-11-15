package models

import play.api.libs.json.{Json, OFormat}

case class Room(
                 roomID: Int,
                 roomNo: Int,
                 floorNo: Int,
                 roomType: String,
                 roomStatus: String,
                 price: Double
               )

object Room {
  implicit val format: OFormat[Room] = Json.format[Room]
}
