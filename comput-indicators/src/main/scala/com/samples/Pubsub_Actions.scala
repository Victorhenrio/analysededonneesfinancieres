// scalastyle:off println
package com.samples

import PubSubServices._
import org.apache.spark.sql.AnalysisException
import org.apache.spark.streaming.Milliseconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.{SparkConf, SparkContext, rdd}
import java.io._


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

    val sparkConf = new SparkConf().setAppName("PubsubAction").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(2000))

    try {
      readPubSub(ssc,"PFE-Data-finnhub","crypto-sub")
    } catch {
      case e: AnalysisException => println("Had an Analysis Exception trying to read pubsub")
      val pw = new PrintWriter(new File("Exception.txt" ))
      pw.write(e.message)
      pw.close
    }


  }


}



// scalastyle:on