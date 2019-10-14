import sbt._

object Dependencies {

  val scalaTestVersion       = "3.0.8"
  val sl4jVersion            = "1.7.27"
  val http4sVersion          = "0.20.8"
  val circeVersion           = "0.11.1"
  val circeDerivationVersion = "0.11.0-M1"
  val pureconfigVersion      = "0.12.1"
  val doobieVersion          = "0.8.4"
  val mysqlConnectorVersion  = "8.0.17"

  lazy val appDeps = Seq(
    "org.slf4j"             % "slf4j-simple"         % sl4jVersion,
    "io.circe"              % "circe-core_2.12"      % circeVersion,
    "org.http4s"            %% "http4s-dsl"          % http4sVersion,
    "org.http4s"            %% "http4s-blaze-server" % http4sVersion,
    "org.http4s"            %% "http4s-circe"        % http4sVersion,
    "io.circe"              %% "circe-derivation"    % circeDerivationVersion,
    "com.github.pureconfig" %% "pureconfig"          % pureconfigVersion,
    "org.tpolecat"          %% "doobie-core"         % doobieVersion,
    "org.tpolecat"          %% "doobie-hikari"       % doobieVersion,
    "mysql"                 % "mysql-connector-java" % mysqlConnectorVersion
  )

  lazy val testDeps = Seq(
    "org.scalatest" %% "scalatest"        % scalaTestVersion,
    "org.http4s"    %% "http4s-circe"     % http4sVersion,
    "org.tpolecat"  %% "doobie-scalatest" % doobieVersion
  ).map(_ % Test)
}
