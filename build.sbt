import com.malliina.sbt.unix.LinuxKeys.{httpPort, httpsPort}
import sbtbuildinfo.BuildInfoKeys.buildInfoPackage

val utilPlayDep = "com.malliina" %% "util-play" % "5.4.1"

val playReverse = Project("play-reverse", file("."))
  .enablePlugins(PlayLinuxPlugin)
  .settings(
    organization := "com.malliina",
    version := "1.2.0",
    scalaVersion := "2.13.1",
    scalacOptions := Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")
    ),
    libraryDependencies ++= Seq(
      ws,
      utilPlayDep,
      utilPlayDep % Test classifier "tests",
      "com.malliina" %% "logstreams-client" % "1.8.2"
    ),
    pipelineStages := Seq(digest, gzip),
    //buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
    buildInfoPackage := "com.malliina.reverse",
    httpPort in Linux := Option("8464"),
    httpsPort in Linux := Option("disabled"),
    maintainer := "Michael Skogberg <malliina123@gmail.com>",
    // WTF?
    linuxPackageSymlinks := linuxPackageSymlinks.value.filterNot(_.link == "/usr/bin/starter"),
    javaOptions in Universal ++= {
      val linuxName = (name in Linux).value
      Seq(
        s"-Dconfig.file=/etc/$linuxName/production.conf",
        s"-Dlogger.file=/etc/$linuxName/logback-prod.xml"
      )
    }
  )
