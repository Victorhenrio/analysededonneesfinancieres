import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.{Done, NotUsed}

import scala.concurrent.{ExecutionContextExecutor, Future}


object mainapi {

  def init()(implicit system: ActorSystem, materializer: ActorMaterializer, executor: ExecutionContextExecutor, messageManager: ActorRef): (ActorRef, Future[Done.type]) = {

    val printSink: Sink[Message, NotUsed] =
      Flow[Message].map {
        case TextMessage.Strict(content) =>
          messageManager ! content
        case _ =>
      }.to(Sink.ignore)

    val messageSource: Source[Message, ActorRef] =
      Source.actorRef[TextMessage.Strict](bufferSize = 32, OverflowStrategy.fail)

    val ((ws, upgradeResponse), closed) =
      messageSource.viaMat(Http().webSocketClientFlow(WebSocketRequest("wss://ws.finnhub.io?token=c0uitof48v6p0uobf4gg")))(Keep.both).toMat(printSink)(Keep.both).run()

    val connected = upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }
    (ws, connected)
  }

  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executor: ExecutionContextExecutor = materializer.executionContext

    val messageManager = system.actorOf(Props(new MessageManager), "manager")
    val symbols = List("BINANCE:BTCUSDT")

    val (act, _) = init()(system, materializer, executor, messageManager)
    //symbols.map(act ! _)
    act ! (TextMessage.Strict("""{"type": "subscribe", "symbol": "BINANCE:BTCUSDT"}"""))

    //messageManager ! (TextMessage.Strict("""{"type": "subscribe", "symbol": "BINANCE:BTCUSDT"}"""))

  }

}
