name := """Travel Partner"""

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)

play.Project.playJavaSettings
