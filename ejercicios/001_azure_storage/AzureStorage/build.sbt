ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "HelloWorld"
  )

libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.15" % "test"

