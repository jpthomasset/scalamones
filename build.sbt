name := "scalamones"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.40-R8"
//libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "1.0"
//libraryDependencies += "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "1.0"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12"
libraryDependencies += "io.spray" % "spray-client_2.11" % "1.3.3"

libraryDependencies += "io.spray" % "spray-json_2.11" % "1.3.2"



fork := true