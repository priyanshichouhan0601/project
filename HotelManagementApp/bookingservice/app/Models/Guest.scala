package models

import play.api.libs.json.{Json, OFormat}

case class Guest(
                  guestId: Long,
                  name: String,
                  roomNo: Int,
                  email: String,
                  address: String,
                  idProof: Array[Byte], // For storing the image as binary data
                  guestStatus: String
                )

object Guest {
  implicit val format: OFormat[Guest] = Json.format[Guest]
}
