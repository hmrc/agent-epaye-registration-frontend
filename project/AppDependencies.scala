import sbt._

object AppDependencies {

  val bootstrapVersion = "7.22.0"
  val mongoVersion = "1.3.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-28" % "8.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % mongoVersion,
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"      % "1.13.0",
    "uk.gov.hmrc"       %% "emailaddress"               % "3.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-15"          % "3.2.11.0",
    "org.mockito"          %% "mockito-scala"            % "1.17.27",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "com.vladsch.flexmark" %  "flexmark-all"             % "0.62.2" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
