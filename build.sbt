import sbt.Keys._

val scalaVersionString = "2.11.6"
val sprayVersion = "1.3.3"
val akkaVersion  = "2.3.12"

val buildSettings = Defaults.coreDefaultSettings ++ Seq(
  organization       := "nl.test",
  version            := "0.0.1-SNAPSHOT",
  scalaVersion       := "2.11.7",
  crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7"),
  resolvers          += Resolver.sonatypeRepo("snapshots"),
  resolvers          += Resolver.sonatypeRepo("releases"),
  scalacOptions     ++= Seq(),
  javacOptions      ++= Seq("-source", "1.7", "-target", "1.7"),
  scalacOptions      += "-target:jvm-1.7"
)

val shapeless = "com.chuusai" %% "shapeless" % "2.2.5"

lazy val macros: Project = (project in file("macros"))
  .settings(buildSettings ++ Seq(
    name                := "macros",
    libraryDependencies += shapeless,
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor >= 11 => libraryDependencies.value
        case Some((2, 10)) =>
          libraryDependencies.value ++ Seq(
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
            "org.scalamacros" %% "quasiquotes" % "2.1.0-M5" cross CrossVersion.binary)
      }
    }
  ))

lazy val core: Project = (project in file("core"))
  .settings(buildSettings ++ Seq(
    name                 := "core",
    fork                 := true,
    libraryDependencies ++= Seq(
      shapeless,
      "org.scala-lang"               % "scala-reflect" % scalaVersionString,
      "com.assembla.scala-incubator" %% "graph-core"   % "1.9.4" withSources() withJavadoc(),
      "org.slf4j" % "slf4j-simple" % "1.7.12" % "test" exclude("org.slf4j", "slf4j-log4j12") withSources() withJavadoc(),
      "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc())
  )) dependsOn(macros)

lazy val root: Project = Project("scala-test-macros", file("."),
  settings = buildSettings ++ Seq(
    run <<= run in Compile in core)
) aggregate(macros, core)
