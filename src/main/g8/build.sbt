import Dependencies._

lazy val root = (project in file("."))
  .settings(
    organization := "com.tahichimzo",
    name := "$name;format="lower,hyphen"$",
    version := "0.0.1",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:higherKinds",
        "-Yno-adapted-args",
        "-Ywarn-unused",
        "-Ywarn-macros:after",
        "-Xfatal-warnings",
        "-Xlint",
        "-Xmacro-settings:materialize-derivations",
        "-Ypartial-unification"
      ),
    javaOptions ++= Seq(
        "-J-XX:+UseConcMarkSweepGC",
        "-J-XX:+CMSParallelRemarkEnabled",
        "-J-XX:+ScavengeBeforeFullGC",
        "-J-XX:+CMSScavengeBeforeRemark",
        "-J-XX:+ExitOnOutOfMemoryError",
        "-J-Xmx3072m"
      ),
    libraryDependencies ++= appDeps ++ testDeps,
    cancelable in Global := true, // make ctrl+c work properly
    scalafmtOnCompile := true,
    assemblyJarName in assembly := "$name;format="lower,hyphen"$.jar",
  )
  .enablePlugins(AssemblyPlugin)
