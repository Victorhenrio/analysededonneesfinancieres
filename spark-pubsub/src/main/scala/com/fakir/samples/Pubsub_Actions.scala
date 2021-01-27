// scalastyle:off println
package com.fakir.samples


import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.pubsub.PubsubUtils
import org.apache.spark.streaming.pubsub.SparkGCPCredentials
import org.apache.spark.streaming.pubsub.SparkPubsubMessage
import org.apache.spark.streaming.Milliseconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.DataFrame
import java.nio.charset.StandardCharsets

import org.apache.spark.sql.types.{DateType, IntegerType}



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

    val sparkConf = new SparkConf().setAppName("PubsubWordCount").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(2000))

    val pubsubStream: DStream[String] = PubsubUtils.createStream(
      ssc, "PFE-data-finance", None, "BTC-sub",
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_ONLY).map(message => new String(message.getData(),StandardCharsets.UTF_8))

    //   val test = pubsubStream.foreachRDD(rdd => {rdd.take(3)})

    val test = pubsubStream.foreachRDD(rdd => rdd.foreach(println))



    //val wordCounts = pubsubStream.collect().foreach(println)
    //wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }

}

// scalastyle:on