package docs.http.scaladsl
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object mainapi {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = "https://finnhub.io/api/v1/crypto/exchange?token=bv63gcn48v6phr4c7370"
    )

    def sendRequest() = {
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
      entityFuture.map(entity => entity.data.utf8String)
    }

  def main(args: Array[String]): Unit = {

    //sendRequest().foreach(println)
    implicit val system = ActorSystem()

    val printSink: Sink[Message, Future[Done]] =
      Sink.foreach {
        case message: TextMessage.Strict =>
          println(message.text)
        case _ =>
      }

    val helloSource: Source[Message, NotUsed] =
      Source.single(TextMessage.Strict("""{"type": "subscribe", "symbol": "BINANCE:BTCUSDT"}"""))

    val flow: Flow[Message, Message, Future[Done]] =
      Flow.fromSinkAndSourceMat(printSink, helloSource)(Keep.left)

    val (upgradeResponse, closed) =
      Http().singleWebSocketRequest(WebSocketRequest("wss://ws.finnhub.io?token=bv63gcn48v6phr4c7370"), flow)

    val connected = upgradeResponse.map { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Done
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }
    connected.onComplete(println)
    closed.foreach(_ => println("closed"))

  }

}
