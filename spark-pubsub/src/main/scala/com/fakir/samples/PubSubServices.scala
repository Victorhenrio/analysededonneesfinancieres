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
import org.apache.spark.sql.functions.{mean, _}
import com.databricks.spark.avro._
import org.apache.spark.rdd.RDD
import org.json4s.jackson.Json

object PubSubServices {

  def readPubSub(ssc: StreamingContext,project:String,subscription:String): Unit = {

    val pubsubStream: DStream[String] = PubsubUtils.createStream(
      ssc, project, None, subscription,
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_ONLY)
      .map(message => new String(message.getData(), StandardCharsets.UTF_8))


    val streamprocess = pubsubStream.foreachRDD { (rdd: RDD[String]) =>

      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      // Convert RDD[String] to DataFrame
      val wordsDataFrame = sqlContext.read.json(rdd)
      wordsDataFrame.show()

      if (wordsDataFrame.rdd.isEmpty != true) {
        wordsDataFrame.printSchema()
        //wordsDataFrame.show()

        val df2 = wordsDataFrame.select(explode(col("data")).as("data")).select("data.*")
        //df2.show(false)

        val df3 = df2.withColumn("date", to_utc_timestamp(from_unixtime(col("t") / 1000, "yyyy-MM-dd HH:mm:ss.SSS"), "EST"))
        df3.show()

        println("New DF :")

        val df4 = df3.groupBy("date","s").agg(mean("p").as("Price")
          ,mean("t").as("Timestamp")
          ,sum("v").as("Volume"))

        df4.show()


        //df3.coalesce(1).write.mode("append").format("json").save("alldata.json")


      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}