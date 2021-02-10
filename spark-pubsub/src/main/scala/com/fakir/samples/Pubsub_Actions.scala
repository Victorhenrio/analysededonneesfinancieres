// scalastyle:off println
package com.fakir.samples

import com.fakir.samples.PubSubServices._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.pubsub.PubsubUtils
import org.apache.spark.streaming.pubsub.SparkGCPCredentials
import org.apache.spark.streaming.pubsub.SparkPubsubMessage
import org.apache.spark.streaming.Milliseconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.{SparkConf, SparkContext, rdd}
import org.apache.spark.sql.DataFrame
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import com.databricks.spark.avro._
import org.apache.spark.rdd.RDD
import org.json4s.jackson.Json

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration


/**
 * Consumes messages from a Google Cloud Pub/Sub subscription and does wordcount.
 * In this example it use application default credentials, so need to use gcloud
 * client to generate token file before running example
 *
 * Usage: PubsubWordCount <projectId> <subscription>
 *   <projectId> is the name of Google cloud
 *   <subscription> is the subscription to a topic
 *
 * Example:
 *  # use gcloud client generate token file
 *  $ gcloud init
 *  $ gcloud auth application-default login
 *
 *  # run the example
 *  $ bin/run-example \
 *      org.apache.spark.examples.streaming.pubsub.PubsubWordCount project_1 subscription_1
 *
 */
object Pubsub_Actions {
  def main(args: Array[String]): Unit = {

    //val Seq(projectId, subscription) = args.toSeq

    val sparkConf = new SparkConf().setAppName("PubsubAction").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(2000))

    implicit val ec = new ExecutionContext {
      val threadPool = Executors.newCachedThreadPool()

      def execute(runnable: Runnable) {
        threadPool.submit(runnable)
      }

      def reportFailure(t: Throwable) {}
    }


    readPubSub(ssc,"pfe-data-finnhub","BTC-topic-sub")
    readPubSub(ssc,"pfe-data-finnhub","ETH-topic-sub")


  }


}



// scalastyle:on