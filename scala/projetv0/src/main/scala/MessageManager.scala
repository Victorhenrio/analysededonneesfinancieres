import Formatteur.{Sample, myStringToJson, parseAsMessage}
import akka.actor.Actor
import com.github.hyjay.pubsub.Publisher



class MessageManager extends Actor{

  override def receive: Receive = {
    case validMessage: Sample =>
      println(s"My actor receive a valid message: $validMessage")
    case message : String =>
      parseAsMessage(message) match {
        case None => println("do nothing")
        case Some(validMessage: Sample) =>
          val message = myStringToJson(validMessage.data(0))
          val io = for {
            // Create a publisher and the returned IO will idempotently create the topic for you.
            publisher <- Publisher.create("pfe-data-finance", "btc-test")
            messageId <- publisher.publish(message.getBytes())
          } yield ()

          io.unsafeRunSync()

      }
    case _ =>
      println("My actor receive something else")
  }

}
