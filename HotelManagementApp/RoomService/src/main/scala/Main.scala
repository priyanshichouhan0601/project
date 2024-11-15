import actors.MailSenderActorSystem.mailSenderActor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import models.{Email, GuestInfo}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import models.JsonFormats._
import spray.json._

object Main {
  private def composeMail(guestInfo: GuestInfo): Email = {
    val body: String = s"Welcome ${guestInfo.name},\n\n It gives us great pleasure to welcome you onboard. We would also like to thank you for choosing us for enhancing your experience." +
      s"Please dial +91-57738 to call the reception any room service, +91-567838 for ordering food." +
      s"Please reach out to +91-68832 in case of any emergency.\n\nWishing for a healthy and pleasant stay.\n\nMy home hotel Priyanshi"
    Email(guestInfo.email, "Welcome to My home Hotel!!!", body)
  }


  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "HotelRoomServiceNotification")

    val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(sys.env.get("BROKER_HOST").getOrElse("localhost")+":9092")
      .withGroupId("roomServiceGroup")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

    Consumer.plainSource(consumerSettings, Subscriptions.topics("hotel_receptions"))
      .map(record => {
        val guest = record.value().parseJson.convertTo[GuestInfo]
        composeMail(guest)
      }) // Convert JSON string to Person
      .runWith(Sink.actorRef(mailSenderActor,
        onCompleteMessage = s"Welcome mail sent to guest",
        onFailureMessage = throwable => s"Error occured: ${throwable.getMessage}"
      ))
  }
}
