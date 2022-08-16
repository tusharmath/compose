import Dependencies._

Global / scalaVersion     := "2.13.8"
ThisBuild / versionScheme := Some("early-semver")

lazy val publishSettings = Seq(
  githubOwner       := "tusharmath",
  githubRepository  := "zio-compose",
  githubTokenSource := TokenSource.GitConfig("github.token"),
)

// Projects
lazy val root = (project in file("."))
  .aggregate(zioCompose, zioComposeMacros)
  .settings(publish / skip := true)
  .settings(publishSettings)

lazy val zioCompose = project
  .in(file("./compose"))
  .settings(publishSettings)
  .settings(
    fork                := true,
    name                := "zio-compose",
    libraryDependencies := Seq(
      ZIOCore,
      ZIOSchema,
      ZIOSchemaJson,
      ZIOSchemaDerivation,
      ZIOTest,
      ZIOTestSbt,
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
  .dependsOn(zioComposeMacros)

lazy val zioComposeMacros = project
  .in(file("./compose-macros"))
  .settings(publishSettings)
  .settings(
    fork                := true,
    name                := "zio-compose",
    libraryDependencies := Seq(
      ZIOSchema,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    ),
  )

// Flags
Global / semanticdbEnabled    := true
Global / onChangedBuildSource := ReloadOnSourceChanges
Global / scalacOptions        := Seq(
  "-Ywarn-unused:imports",
  "-Werror",
  "-feature",
  "-language:reflectiveCalls",
)
