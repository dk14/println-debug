
import sbt._
import Keys._

object MyBuild extends Build {

  lazy val projectSettings = Seq(
    autoCompilerPlugins := true,
    scalaVersion := "2.11.0",
    resolvers in ThisBuild  += Resolver.sonatypeRepo("releases"),
    libraryDependencies += "org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
  )

  lazy val macro = Project(id = "macro", base = file("./macro"), settings = projectSettings)
  lazy val test = Project(id = "test", base = file("./test"), settings = projectSettings) dependsOn macro
  lazy val root = project.in(file(".")) aggregate(macro, test)
}