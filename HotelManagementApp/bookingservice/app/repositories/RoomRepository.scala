package repositories

import models.Room
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

class RoomRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class RoomTable(tag: Tag) extends Table[Room](tag, "Room") {
    def roomID = column[Int]("RoomID", O.PrimaryKey)
    def roomNo = column[Int]("RoomNo")
    def floorNo = column[Int]("Floor_no")
    def roomType = column[String]("Room_type")
    def roomStatus = column[String]("Room_status")
    def price = column[Double]("Price")

    def * = (roomID, roomNo, floorNo, roomType, roomStatus, price) <> ((Room.apply _).tupled, Room.unapply)
  }

  private val rooms = TableQuery[RoomTable]

  // Method to get all available rooms by type
  def getAvailableRoomsByType(roomType: String): Future[Seq[Room]] = db.run {
    rooms
      .filter(room => room.roomStatus === "AVAILABLE" && room.roomType === roomType)
      .result
  }

  // Method to update room status by RoomID
  def updateRoomStatusById(roomId: Int, status: String): Future[Int] = db.run {
    rooms.filter(_.roomID === roomId).map(_.roomStatus).update(status)
  }

  // Method to update room status by RoomNo
  def updateRoomStatusByRoomNo(roomNo: Int, status: String): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.roomStatus).update(status)
  }

  def getRoomIdByRoomNo(roomNo: Int): Future[Option[Int]] = db.run {
    rooms.filter(_.roomNo === roomNo).filter(_.roomStatus === "AVAILABLE").map(_.roomID).result.headOption
  }

  // Method to update the room status to OCCUPIED
  def updateRoomStatus(roomNo: Int, status: String = "OCCUPIED"): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.roomStatus).update(status)
  }

  def getRoomNoById(roomId: Int): Future[Option[Int]] = db.run {
    rooms.filter(_.roomID === roomId).map(_.roomNo).result.headOption
  }
}
