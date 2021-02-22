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
import com.mongodb.spark.MongoSpark
import org.apache.spark.rdd.RDD
import org.json4s.jackson.Json

import scala.math.BigDecimal.long2bigDecimal


object PubSubServices {
  var compteur = 0
  def readPubSub(ssc: StreamingContext,project:String,subscription:String): Unit = {

    val pubsubStream: DStream[String] = PubsubUtils.createStream(
      ssc, project, None, subscription,
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_ONLY)
      .map(message => new String(message.getData(), StandardCharsets.UTF_8))


    val streamprocess = pubsubStream.foreachRDD { (rdd: RDD[String]) =>

      val spark = SparkSession.builder()
        .master("local")
        .appName("MongoSparkConnectorIntro")
        .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/newdb.collection?authSource=admin")
        .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/newdb.collection?authSource=admin")
        .getOrCreate()

      // Convert RDD[String] to DataFrame
      val wordsDataFrame = spark.read.json(rdd)
      //wordsDataFrame.show()

      if (wordsDataFrame.rdd.isEmpty != true) {
        wordsDataFrame.printSchema()
        //wordsDataFrame.show()

        val dataDF = wordsDataFrame.select(explode(col("data")).as("data")).select("data.*")
        //df2.show(false)

        val dfWithDate = dataDF.withColumn("date", to_utc_timestamp(from_unixtime(col("t") / 1000, "yyyy-MM-dd HH:mm:ss"), "Europe/Paris"))
        val dfNewTime = dfWithDate.withColumn("new_time",round(col("t")/1000,0))
        val fullDF = dfNewTime.withColumn("symbol",col("s"))
        //df5.show()

        val groupedDF = fullDF.groupBy("new_time","symbol").agg(mean("p").as("price")
          ,sum("v").as("volume"),(col("new_time")*1000).as("time_updated"))

        groupedDF.select("time_updated").show()

        groupedDF.write.mode("append").mongo()


        // Read from Mongo

        //val mongoRDD = MongoSpark.load(spark)

       // val mongoDF = mongoRDD.toDF()
        //mongoDF.show()

        val currentTime = System.currentTimeMillis
        val currentTimeDecimal = (currentTime/1000).setScale(0)
        val currentTimeInSeconds = currentTimeDecimal*1000 - 360000

        println(currentTimeInSeconds)

        compteur = compteur + 1
        println("Compteur value : " , compteur)

        if (compteur>1){
          val mongoRDDTest = MongoSpark.load(spark)
          val mongoDFtest = mongoRDDTest.toDF()

          println("ca se passe ici")
          println(currentTimeInSeconds-1000)
          mongoDFtest.filter(mongoDFtest("time_updated") > currentTimeInSeconds-60000).show()

          println("mais pas la")
          mongoDFtest.select("price").show()
        }
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}

//case class df4(word: String, count: Int)