package com.fakir.samples

import com.mongodb.spark.sql._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.pubsub.{PubsubUtils, SparkGCPCredentials}
import java.nio.charset.StandardCharsets
import org.apache.spark.sql.SparkSession

object mongodbConnect {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("PubsubAction").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(2000))


    val lines: DStream[String] = PubsubUtils.createStream(
      ssc, "PFE-Data-Finnhub", None, "BTC-sub",
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_ONLY)
      .map(message => new String(message.getData(),StandardCharsets.UTF_8))

    val test = lines.foreachRDD(rdd => rdd.foreach(println))
    println("etape 1")

    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    println("etape 1.1")


    wordCounts.foreachRDD({ rdd =>
      println("etape 2")
      val spark = SparkSession.builder()
        .master("local")
        .appName("MongoSparkConnectorIntro")
        .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/test.collection?authSource=admin")
        .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/test.collection?authSource=admin")
        .getOrCreate()
      import spark.implicits._
      rdd.take(1)
      val wordCounts = rdd.map({ case (word: String, count: Int)
      => WordCount(word, count) }).toDF()
      wordCounts.write.mode("append").mongo()
      println("etape 3")

    })

    ssc.start()
    ssc.awaitTermination()

  }

}

//case class WordCount(word: String, count: Int)