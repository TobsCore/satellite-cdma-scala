name := "CDMA Decoder"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

mainClass in (Compile, run) := Some("hska.embedded.Application")

