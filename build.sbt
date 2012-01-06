organization := "nl.surfnet"

name := "sos-server"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

port in container.Configuration := 8081

{
  val scalatraVersion = "2.0.2"
  libraryDependencies ++= Seq(
    "org.scalatra" %% "scalatra" % scalatraVersion,
    "org.scalatra" %% "scalatra-lift-json" % scalatraVersion ,
    "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test",
    "net.liftweb" %% "lift-json" % "2.4-M5",
    "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime",
    "org.clapper" %% "grizzled-slf4j" % "0.6.6",
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.0.RC2" % "container",
    "javax.servlet" % "servlet-api" % "2.5" % "provided"
  )
}

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
