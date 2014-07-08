name := """TravelPartner"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  javaWs,
  cache,
  "com.google.inject" % "guice" % "4.0-beta4",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
  )
  
  fork in Test := false