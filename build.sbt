name := "ActorFundamentals"

version := "0.1"

scalaVersion := "2.12.10"

val akkaVersion = "2.5.32"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
)