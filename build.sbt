import play.Project._

name := """Travel Partner"""

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  javaWs,
  cache,
  "com.google.inject" % "guice" % "4.0-beta4",
  "org.imgscalr" % "imgscalr-lib" % "4.2"
)

play.Project.playJavaSettings :+ (Keys.fork in (Test) := false)