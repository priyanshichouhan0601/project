package jobs

import javax.inject._
import play.api.Logging
import repositories.{BookingDetailsRepository, GuestRepository, RoomRepository}
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import java.time.LocalDate

@Singleton
class RoomStatusUpdateTask @Inject()(
                                      bookingDetailsRepository: BookingDetailsRepository,
                                      roomRepository: RoomRepository,
                                      guestRepository: GuestRepository
                                    )(implicit ec: ExecutionContext) extends Logging {

  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  // Schedule the cron job to run daily at midnight
  scheduler.scheduleAtFixedRate(() => checkAndUpdateRoomAndGuestStatus(), 0, 1, TimeUnit.DAYS)

  def checkAndUpdateRoomAndGuestStatus(): Future[Unit] = {
    val today = LocalDate.now()
    logger.info(s"Cron job started: Checking room and guest statuses for $today")

    bookingDetailsRepository.getBookingsEndingToday(today).flatMap { bookingsEndingToday =>
      Future.sequence(bookingsEndingToday.map { booking =>
        for {
          // Step 1: Get the room number for the current room ID
          roomNoOption <- roomRepository.getRoomNoById(booking.roomId)

          // Step 2: Update guest statuses to "INACTIVE" if roomNo is found
          _ <- roomNoOption match {
            case Some(roomNo) =>
              guestRepository.updateGuestsStatusByRoomNo(roomNo, "INACTIVE")
            case None =>
              Future.successful(()) // No action needed if roomNo is None
          }

          // Step 3: Update the room status to "AVAILABLE"
          _ <- roomRepository.updateRoomStatusById(booking.roomId, "AVAILABLE")
        } yield ()
      })
    }.map { _ =>
      logger.info("Cron job completed: Room and guest statuses updated for rooms with end_date today.")
    }.recover {
      case ex: Exception =>
        logger.error("Cron job failed", ex)
    }
  }
}
