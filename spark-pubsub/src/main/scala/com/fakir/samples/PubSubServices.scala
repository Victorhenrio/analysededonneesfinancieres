package com.fakir.samples

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream}
import org.apache.spark.streaming.pubsub.PubsubUtils
import org.apache.spark.streaming.pubsub.SparkGCPCredentials
import org.apache.spark.streaming.StreamingContext
import java.nio.charset.StandardCharsets

import com.mongodb.spark.sql._
import org.apache.spark.sql._
import org.apache.spark.sql.functions.{mean, _}
import com.mongodb.spark.MongoSpark
import org.apache.spark.rdd.RDD



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
        .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/test.collection?authSource=admin")
        .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/test.collection?authSource=admin")
        .getOrCreate()

      // Convert RDD[String] to DataFrame
      val wordsDataFrame = spark.read.json(rdd)

      if (wordsDataFrame.rdd.isEmpty != true) {
        wordsDataFrame.printSchema()


        val dataDF = wordsDataFrame.select(explode(col("data")).as("data")).select("data.*")

        val dfNewTime = dataDF.withColumn("new_time",round(col("t")/1000,0))
        val fullDF = dfNewTime.withColumn("symbol",col("s"))

        val groupedDF = fullDF.groupBy("new_time","symbol").agg(mean("p").as("price")
          ,sum("v").as("volume"),(col("new_time")*1000).as("unix_time"))
        val DF = groupedDF.drop("new_time")

        val dfWithDate = DF.withColumn("date", to_utc_timestamp(from_unixtime(col("unix_time") / 1000, "yyyy-MM-dd HH:mm:ss"), "Europe/Paris"))
        dfWithDate.show()

        dfWithDate.select("date").show()
        dfWithDate.write.mode("append").mongo()


        // READ FROM MONGO


          val currentTime = System.currentTimeMillis

          val mongoRDDTest = MongoSpark.load(spark)
          val mongoDFtest = mongoRDDTest.toDF()

          mongoDFtest.filter(mongoDFtest("unix_time") > currentTime-200000).show()

//        mongoDFtest.select("price").show()
        }
      }
    ssc.start()
    ssc.awaitTermination()
    }
}


