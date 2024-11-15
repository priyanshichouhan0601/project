package models

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}

case class BookingDetails(
                           bookingId: Int,
                           guestId: Long,
                           roomId: Int,
                           startDate: LocalDate,
                           endDate: LocalDate
                         )

object BookingDetails {
  implicit val format: OFormat[BookingDetails] = Json.format[BookingDetails]
}
