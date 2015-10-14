organization := "com.thoughtworks"

name := "remote-pair-server"

version := "0.3.0"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls", "scalac.patmat.analysisBudget=512")

sbtVersion := "0.13.9"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

resolvers ++= Seq(
  "Scalaz" at "http://dl.bintray.com/scalaz/releases",
  "spray repo" at "http://repo.spray.io",
  "ibiblio" at "http://mirrors.ibiblio.org/pub/mirrors/maven2",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
  "akka" at "http://repo.akka.io",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-remote" % "2.3.12",
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.json4s" %% "json4s-core" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "org.specs2" %% "specs2" % "2.4.11" % "test",
  "io.netty" % "netty-all" % "5.0.0.Alpha1"
)

mainClass in Compile := Some("com.thoughtworks.pli.intellij.remotepair.server.StandaloneServer")

initialize ~= { _ =>
  if (System.getProperty("versionCheck", "true").toBoolean) {
    val specVersion = sys.props("java.specification.version")
    println(s"Detected Java version: $specVersion")
    require(specVersion == "1.7", "Jdk 1.7.x is required")
  }
}

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.thoughtworks.pli.intellij.remotepair"
  )
