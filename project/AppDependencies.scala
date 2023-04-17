import sbt._

object AppDependencies {

  val bootrapVersion = "7.15.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.3.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-27"             % "0.59.0",
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"          % "1.2.0",
    "uk.gov.hmrc"       %% "emailaddress"                   % "3.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"  % bootrapVersion,
    "org.scalatestplus"       %% "scalacheck-1-15"         % "3.2.11.0",
    "org.mockito"             %% "mockito-scala"           % "1.17.14",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28" % "0.74.0",
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.62.2" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
