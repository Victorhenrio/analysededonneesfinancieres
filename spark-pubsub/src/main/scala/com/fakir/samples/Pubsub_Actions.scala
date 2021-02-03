// scalastyle:off println
package com.fakir.samples


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

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import com.databricks.spark.avro._
import org.apache.spark.rdd.RDD
import org.json4s.jackson.Json




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
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_ONLY)
      .map(message => new String(message.getData(),StandardCharsets.UTF_8))


    val streamprocess =   pubsubStream.foreachRDD { (rdd: RDD[String]) =>

      // Get the singleton instance of SparkSession
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      // Convert RDD[String] to DataFrame
      val wordsDataFrame = sqlContext.read.json(rdd)
      //wordsDataFrame.show()

      if (wordsDataFrame.rdd.isEmpty != true) {
        wordsDataFrame.printSchema()
        //wordsDataFrame.show()

        val df2 = wordsDataFrame.select(explode(col("data")).as("data")).select("data.*")
        //df2.show(false)

        val df3 = df2.withColumn("date", to_utc_timestamp(from_unixtime(col("t") / 1000, "yyyy-MM-dd HH:mm:ss.SSS"), "EST"))
        df3.show()
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }

}


/** Case class for converting RDD to DataFrame */
case class Message(data: Json)


/** Lazily instantiated singleton instance of SparkSession */
object SparkSessionSingleton {

  @transient  private var instance: SparkSession = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()
    }
    instance
  }
}

// scalastyle:on