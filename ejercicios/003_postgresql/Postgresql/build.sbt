ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "Postgresql"
  )

libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.18" % "test"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.1"