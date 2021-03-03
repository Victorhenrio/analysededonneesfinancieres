import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

object Formatteur {

  case class Data( c: Option[String], p: Double, s: String, t: Long, v: Double)

  case class Sample(data: List[Data])

  implicit val validMessageReaderdata = Json.reads[Data]
  implicit val validMessageReader = Json.reads[Sample]

  implicit val validMessageFormatteur = Json.format[Data]


  def myStringToValidMessage(jsonStr: String) : Sample = Json.parse(jsonStr).as[Sample]

  def myStringToJson(data: Data) : String = Json.toJson(data).toString()

  def parseAsMessage(jsonStr: String): Option[Sample] = {
    Try(Json.parse(jsonStr).as[Sample]) match {
      case Success(value) => Some(value)
      case Failure(exception) => None
    }
  }

}
