package com.fakir.samples.reader

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.pubsub.{PubsubUtils, SparkGCPCredentials, SparkPubsubMessage}

object PubsubWordCount {

  def sub(args: Array[String], ssc: StreamingContext): Unit = {

    val Seq(projectId, subscription) = args.toSeq


    val pubsubStream: ReceiverInputDStream[SparkPubsubMessage] = PubsubUtils.createStream(
      ssc, projectId, None, subscription,
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_AND_DISK_SER_2)

    val wordCounts = pubsubStream

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }

}
