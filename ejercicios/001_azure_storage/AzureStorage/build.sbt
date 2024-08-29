ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "HelloWorld"
  )

libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.15" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies += "com.azure" % "azure-storage-blob" % "12.17.0"
libraryDependencies += "com.azure" % "azure-data-tables" % "12.2.0"

