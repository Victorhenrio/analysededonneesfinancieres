package com.fakir.samples

import java.nio.charset.StandardCharsets

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.sql._
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.pubsub.{PubsubUtils, SparkGCPCredentials}


object MongoWithPubSub {

  def mongoread(df: DataFrame): Unit = {

    val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/totor.collection?authSource=admin")
      .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/totor.collection?authSource=admin")
      .getOrCreate()

    println("RDD 1 : ")
    val rdd = MongoSpark.load(spark)
    println(rdd.count)
    println(rdd.first)

    val rdd2 = spark.loadFromMongoDB()
    println("RDD 2 : ")
    println(rdd2)

  }

}