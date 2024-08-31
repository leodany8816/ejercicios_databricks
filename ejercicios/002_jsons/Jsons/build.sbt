ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "Jsons"
  )

libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.15" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies += "com.azure" % "azure-storage-blob" % "12.17.0"
libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"