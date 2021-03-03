import java.util.Base64

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.googlecloud.pubsub.scaladsl.GooglePubSub
import akka.stream.alpakka.googlecloud.pubsub.{PubSubConfig, PublishMessage, PublishRequest}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future


object testpub {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    val privateKey =
      """-----BEGIN RSA PRIVATE KEY-----
        |MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQDLK2yDIp8VuDgZ
        |3ZH9Ttb0xNwacodYx0PIwQAHiJ032OmWq0+FtlaX0LygLJhwSyL29uDtTbbdAdwH
        |ry4AWhSUXwlxe5jmRiioV46MhCKKrG/vGqj+rqKQjCo6WIR+XXMJSF7qz0D79dT/
        |tdAN3QpN9fqBwkQQ5s/a+ynlJAf7f3nxphHJNidptpbwOqE4yrspyKV2QfgDflTZ
        |uxiKTYrReGJ1scVwyQk2lGZUt9QWtNvgaAIe8xr3+cHg3vfzDN0hH60PfThT5mn7
        |sSmHaa8YgezIpYy7yMdPUCuqmlvwq7s4XtEnu20r7W5pKWBNPdG8RDfxRvjKKifb
        |WqMdGV2pAgMBAAECggEAKetRvlTcYgzadmvC/XLQ4zvi9gn/Zw/mKnu319YWKjm1
        |KIM8KyiOAVDuOlY5NaIrGq2hhxTrz/ncPjGk2im76Ukal7GawJhlNyapoNB6mAYw
        |KjNj/vhlTlG9PMCaxb/eRmIKQ4Rzsgjs473Nqa6bu64u+6rrhFk7NcFwyXh/tSIf
        |7IidBMSKSvJAv3eyRaSjtlAFWyLQZO1lAdc4MMR+jWpNSENZ+vz9KEeKKxGVkbS9
        |a3NvHOfV86a9YeKDWbI3V6AHuy+a5t75h60EqqnXTMdPGzBcAHYHMD4A6bjMHcpH
        |2IhXuH7jRWsh+bKY+ycg7ZxOmr5EpIljchNcSE7E6QKBgQDyRf87L824X7z8iH0H
        |073VXlbHHpc4RyyZTjNY6EcJA9o575q7sOBgS91288+JcWSQk1lXrIaEjPW+zpV8
        |KQkVLeWC/WCqzyZShmvGpyaPWoXU02XOpAPIkIokBQQO+Rv7CYFoboa5mPGS/8hw
        |faE2HHQfwH3/7Bhu50SMFOiOnwKBgQDWrkERVp+yB85xl8grEiX52D96PtMoszxg
        |3+ziIKFidA2A9sMM9jARqcOmIzszwFfFG0mcFwozOTYMhpHuqkryNacmVkeaTsIg
        |8x8PqA/k9xKMm7XRih5v4N3cyitgChuDstFwbpBgCjXqFlTiEizLF43T+isXB65x
        |XMP+Wc5WtwKBgHFyFXHBkLMlIXme7+0aWWnwIvJagLT5nwiqdaDMI3c1Npqqd+or
        |iOWOoMVypVvWnSCV3uAPCW5IE0qlsZnSHU6tjysnOnzBQ1ChYRZEbunxmXlvA90f
        |MCZaiFUVhDD+tPt4GACuwKdn1rbezxzdtv9/k2DK4jVUeJx17sMphzxBAn8f3b3K
        |1RDfXElIkz8jJY59h5vkiW7Om8xalOKUrkNGWfOnMPnC8sgv1wMzpgcjp2lf2K0U
        |flcFQ3jCYzdGNgDp8wzco/H89bPbMDtsF4ZEdmhKdtKGR/o8oSRNobTOjLGI2wI7
        |ri3AE8Ps+2OeKxR7EQP8cyXyRTGfoiV1m98FAoGBAOmVmSkv1E8BBZErkYsa9Fez
        |FAYvALOiQQUqs1HFJhRi1rERhqKZ/yrdE9KDAZfHhLGj52Q0Cl6mYGfxSByo7W+F
        |C/+mz22IGWwiySXHqLjQpbHK0n22XGGhbFDBGlNPiRWvWq7V/gYMqXD0tYxhioJV
        |eCVcWmqKmkEEyPspapaW
        |-----END RSA PRIVATE KEY-----""".stripMargin

    val clientEmail = "dataproc-service-account@pfe-data-finance.iam.gserviceaccount.com"
    val projectId = "pfe-data-finance"
    val apiKey = "AIzaSyBlFqBwnhhwNk5a4-TBTxijNPj_t5tdI4c"

    val config = PubSubConfig(projectId, clientEmail, privateKey)

    val topic = "projects/pfe-data-finance/topics/finnhub-finance"

    val publishMessage =
      PublishMessage(new String(Base64.getEncoder.encode("Hello Google!".getBytes)))
    val publishRequest = PublishRequest(scala.collection.immutable.Seq(publishMessage))

    val source: Source[PublishRequest, NotUsed] = Source.single(publishRequest)

    val publishFlow: Flow[PublishRequest, Seq[String], NotUsed] =
      GooglePubSub.publish(topic, config)

    val publishedMessageIds: Future[Seq[Seq[String]]] = source.via(publishFlow).runWith(Sink.seq)



  }



}
