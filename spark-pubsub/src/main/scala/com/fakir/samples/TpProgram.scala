package com.fakir.samples

//import com.fakir.samples.reader.PubsubWordCount
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._

import org.apache.spark.streaming.Milliseconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf

object TpProgram {


  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)


    val spark = SparkSession.builder().master("local").getOrCreate()
    //"C:\Users\Victor HENRIO\Documents\PFE\scala-spark-PFE\conf\btc.json"
    val path = "C:\\Users\\Victor HENRIO\\Documents\\PFE\\scala-spark-PFE\\conf\\btc_simple.json"

    val df:DataFrame  = spark.read.json(path).toDF()
    df.printSchema()
    df.show()

    val df2 = df.select(explode(col("data")).as("data")).select("data.*")
    df2.show(false)


    val df3 = df2.withColumn("date", to_utc_timestamp(from_unixtime(col("t")/1000,"yyyy-MM-dd HH:mm:ss.SSS"),"EST"))
    df3.show()


    // POUR GARDER LES MILLISECONDES :

//    val df4 = df2.withColumn("date_mili", ZonedDateTime.ofInstant(Instant.ofEpochMilli(col("t")), ZoneId.of("GMT+1")),"EST"))
//    df3.show()
//
//    val timeInMillis = System.currentTimeMillis()
//    //timeInMillis: Long = 1486988060666
//
//    val instant = Instant.ofEpochMilli(timeInMillis)
//    //instant: java.time.Instant = 2017-02-13T12:14:20.666Z
//
//    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
//    //zonedDateTimeUtc: java.time.ZonedDateTime = 2017-02-13T12:14:20.666Z[UTC]
//    println(zonedDateTimeUtc)

//      spark.stop() //On stop le SparkContext car un seul peut fonctionner à la foi
//
//      val conf = new SparkConf().setMaster("local[2]").setAppName("PubsubWordCount")
//
//      val ssc = new StreamingContext(conf, Milliseconds(2000))
//
//      val args = Array("utility-destiny-300118", "my-topic")
//
//      PubsubWordCount.sub(args,ssc)


  }
}
