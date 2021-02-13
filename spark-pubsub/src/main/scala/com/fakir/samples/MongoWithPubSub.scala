package com.fakir.samples

import java.nio.charset.StandardCharsets

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
      .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118:27017/new.collection?authSource=admin")
      .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118:27017/new.collection?authSource=admin")
      .config("spark.mongodb.output.database","test")
      .getOrCreate()

    val df1 = df
    import spark.implicits._
    df1.write.mode("append").mongo()
    //df.write.mode("append").option("database","test").option("collection", "collection").mongo()

  }

}