package com.mongodb
object GettingStarted {
  def main(args: Array[String]): Unit = {
    /* Create the SparkSession.
     * If config arguments are passed from the command line using --conf,
     * parse args for the values to set.
     */
    import org.apache.spark.sql.SparkSession
    val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri", "mongodb://root:toto@34.94.185.118/testdb")
      .config("spark.mongodb.output.uri", "mongodb://root:toto@34.94.185.118/testdb")
      .getOrCreate()
  }
}