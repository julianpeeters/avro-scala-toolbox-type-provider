name := "avro-scala-toolbox-type-provider"

version := "0.1"

organization := "com.julianpeeters"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq( 
  "org.apache.avro" % "avro" % "1.7.6",
  "com.julianpeeters" %% "toolbox-type-provider" % "0.1",
  "org.specs2" %% "specs2" % "2.2" % "test",
  "com.novus" %% "salat" % "1.9.8" % "test"
)
