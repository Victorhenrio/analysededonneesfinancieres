name := "scala-spark-pubsub-mongodb"

version := "0.1"

scalaVersion := "2.11.11"  //2.12.13

val sparkVersion = "2.2.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0" //2.4.0
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"  //2.4.0
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.2.0" //2.4.0 Dans le doute (pas de relation avec scala version)
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.1" //2.4.0
libraryDependencies += "org.apache.bahir" %% "spark-streaming-pubsub" % "2.2.0" //2.4.0
libraryDependencies += "org.mongodb.spark" %% "mongo-spark-connector" % "2.2.0" //2.4.0


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

//JAR file settings
assemblyOption in assembly := (assemblyOption in  assembly).value.copy(includeScala = true)
assemblyJarName in assembly := s"${name.value}_${scalaBinaryVersion.value}-${sparkVersion}_${version.value}.jar"
