import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "0.94.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.6.0-play-27",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.24.0",
    "uk.gov.hmrc"       %% "play-language"                  % "5.3.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-27"             % "0.59.0",
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"          % "0.42.0-play-27",
    "uk.gov.hmrc"       %% "emailaddress"                   % "3.6.0"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"               % "3.2.12",
    "org.scalatestplus"       %% "scalacheck-1-15"         % "3.2.11.0",
    "org.scalatestplus"       %% "mockito-3-4"             % "3.2.10.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"             %  "pegdown"                 % "1.6.0",
    "org.jsoup"               %  "jsoup"                   % "1.15.1",
    "com.typesafe.play"       %% "play-test"               % PlayVersion.current,
    "org.mockito"             %% "mockito-scala"           % "1.17.7",
    "org.scalacheck"          %% "scalacheck"              % "1.16.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-27" % "0.59.0",
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.62.2" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
