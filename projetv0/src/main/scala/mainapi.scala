import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object mainapi {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"

    )

    def sendRequest() = {
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
      entityFuture.map(entity => entity.data.utf8String)
    }

  def main(args: Array[String]): Unit = {

    sendRequest().foreach(println)

  }

}
