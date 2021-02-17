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
import com.mongodb.spark.sql._

import com.fakir.samples.MongoWithPubSub.mongoread
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

      val spark = SparkSession.builder()
        .master("local")
        .appName("MongoSparkConnectorIntro")
        .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/totor.collection?authSource=admin")
        .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/totor.collection?authSource=admin")
        .getOrCreate()

      // Convert RDD[String] to DataFrame
      val wordsDataFrame = spark.read.json(rdd)
      wordsDataFrame.show()

      if (wordsDataFrame.rdd.isEmpty != true) {
        wordsDataFrame.printSchema()
        //wordsDataFrame.show()

        val df2 = wordsDataFrame.select(explode(col("data")).as("data")).select("data.*")
        //df2.show(false)

        val df3 = df2.withColumn("date", to_utc_timestamp(from_unixtime(col("t") / 1000, "yyyy-MM-dd HH:mm:ss"), "Europe/Paris"))
        val df4 = df3.withColumn("new_time",round(col("t")/1000,0))
        val df5 = df4.withColumn("symbol",col("s"))


        val df6 = df5.groupBy("new_time","symbol").agg(mean("p").as("price")
          ,sum("v").as("volume"),(col("new_time")*1000).as("time_updated"))

        df6.show()
        df6.write.mode("append").mongo()
        //mongoread(df4)


      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}

//case class df4(word: String, count: Int)