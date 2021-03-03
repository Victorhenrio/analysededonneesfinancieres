

name := "projetv0"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.31"
val akkaHttpVersion = "10.1.11"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.play" %% "play-json" % "2.9.0",
    "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub" % "2.0.2",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.github.hyjay" %% "fs2-google-cloud-pubsub" % "0.1.0"

  )
}