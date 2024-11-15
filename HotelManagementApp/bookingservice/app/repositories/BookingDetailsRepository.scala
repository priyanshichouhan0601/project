package repositories

import models.BookingDetails
import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

class BookingDetailsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class BookingDetailsTable(tag: Tag) extends Table[BookingDetails](tag, "BookingDetails") {
    def bookingId = column[Int]("booking_id", O.PrimaryKey, O.AutoInc) // Int to match the model
    def guestId = column[Long]("guest_id")
    def roomId = column[Int]("room_id") // Ensure this is Int if roomId in Room is also Int
    def startDate = column[LocalDate]("start_date")
    def endDate = column[LocalDate]("end_date")

    def * = (bookingId, guestId, roomId, startDate, endDate) <> ((BookingDetails.apply _).tupled, BookingDetails.unapply)
  }

  private val bookingDetails = TableQuery[BookingDetailsTable]

  // Method to find booking by ID
  def findBookingById(bookingId: Int): Future[Option[BookingDetails]] = db.run {
    bookingDetails.filter(_.bookingId === bookingId).result.headOption
  }

  // Method to get all bookings ending today
  def getBookingsEndingToday: Future[Seq[BookingDetails]] = db.run {
    bookingDetails.filter(_.endDate === LocalDate.now()).result
  }

  // Method to delete booking by ID
  def deleteBooking(bookingId: Int): Future[Int] = db.run {
    bookingDetails.filter(_.bookingId === bookingId).delete
  }

  def addBooking(booking: BookingDetails): Future[Int] = db.run {
    (bookingDetails returning bookingDetails.map(_.bookingId)) += booking
  }

  def getBookingsEndingToday(today: LocalDate): Future[Seq[BookingDetails]] = db.run {
    bookingDetails.filter(_.endDate === today).result
  }
}
