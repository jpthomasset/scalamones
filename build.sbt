name := "scalamones"

version := "1.0"

scalaVersion := "2.11.6"

// Scala FX
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.40-R8"

// FXML
addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2"

// Akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.12"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.12"

// Spray
libraryDependencies += "io.spray" %% "spray-client" % "1.3.3"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

// Test lib
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

scalacOptions ++= Seq("-feature")
scalacOptions += "-target:jvm-1.8"

// Prevent startup bug in JavaFX
fork := true

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}