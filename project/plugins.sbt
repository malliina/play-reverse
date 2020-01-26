scalaVersion := "2.12.10"

scalacOptions ++= Seq("-unchecked", "-deprecation")

Seq(
  "com.malliina" % "sbt-play" % "1.7.5",
  "com.typesafe.sbt" % "sbt-digest" % "1.1.4",
  "com.typesafe.sbt" % "sbt-gzip" % "1.0.2"
) map addSbtPlugin
