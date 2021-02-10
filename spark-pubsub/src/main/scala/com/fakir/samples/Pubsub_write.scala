package com.fakir.samples

import scala.collection.JavaConverters._
import scala.util.Random

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.pubsub.Pubsub.Builder
import com.google.api.services.pubsub.model.PublishRequest
import com.google.api.services.pubsub.model.PubsubMessage
import com.google.cloud.hadoop.util.RetryHttpInitializer

import org.apache.spark.streaming.pubsub.SparkGCPCredentials



object Pubsub_write {
  def main(args: Array[String]): Unit = {

    //val Seq(projectId, topic, recordsPerSecond) = args.toSeq

    val APP_NAME = this.getClass.getSimpleName

    val client = new Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance(),
      new RetryHttpInitializer(
        SparkGCPCredentials.builder.build().provider,
        APP_NAME
      ))
      .setApplicationName(APP_NAME)
      .build()


    val randomWords = List("google", "cloud", "pubsub", "say", "hello")
    val publishRequest = new PublishRequest()
    for (i <- 1 to 10) {
      val messages = (1 to 1).map { recordNum =>
        val randomWordIndex = Random.nextInt(randomWords.size)
        new PubsubMessage().encodeData(randomWords(randomWordIndex).getBytes())
      }
      publishRequest.setMessages(messages.asJava)
      client.projects().topics()
        .publish(s"projects/pfe-data-finnhub/topics/finnhub-finance", publishRequest)
        .execute()
      println(s"Published data. topic: finnhub-finance; Mesaage: $publishRequest")

      Thread.sleep(1000)
    }

  }
}