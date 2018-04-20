import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import com.malliina.sbtplay.PlayProject
import sbtbuildinfo.BuildInfoKey
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoPackage}

lazy val p = PlayProject.server("play-reverse")

val utilPlayDep = "com.malliina" %% "util-play" % "4.11.1"

organization := "com.malliina"
version := "1.0.0"
scalaVersion := "2.12.5"
scalacOptions := Seq("-unchecked", "-deprecation")
resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.bintrayRepo("malliina", "maven")
)
libraryDependencies ++= Seq(
  ws,
  utilPlayDep,
  utilPlayDep % Test classifier "tests",
  "com.malliina" %% "logstreams-client" % "1.0.0"
)

pipelineStages := Seq(digest, gzip)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.malliina.reverse"

httpPort in Linux := Option("8464")
httpsPort in Linux := Option("disabled")
maintainer := "Michael Skogberg <malliina123@gmail.com>"
// WTF?
linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot(_.link == "/usr/bin/starter")
javaOptions in Universal ++= {
  val linuxName = (name in Linux).value
  Seq(
    s"-Dconfig.file=/etc/$linuxName/production.conf",
    s"-Dlogger.file=/etc/$linuxName/logback-prod.xml"
  )
}
