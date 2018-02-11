name := """sremote"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "4th Line" at "http://4thline.org/m2"

libraryDependencies ++= Seq(
 javaJdbc,
  javaJpa,
  jdbc,
  anorm,
  cache,
  ws,
  "com.google.inject" % "guice" % "3.0",
  "com.google.inject.extensions" % "guice-multibindings" % "3.0",
  "org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final",
  "org.teleal.cling" % "cling-core" % "1.0.5",
  "org.teleal.cling" % "cling-support" % "1.0.5",
  "com.etaty.rediscala" %% "rediscala" % "1.3.1",
  "org.scalaj" %% "scalaj-http" % "0.3.16"
)
