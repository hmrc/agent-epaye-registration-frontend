import sbt._

object AppDependencies {

  val bootstrapVersion = "8.4.0"
  val mongoVersion = "1.7.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "9.3.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"      % "2.0.0",
    "uk.gov.hmrc"       %% "emailaddress"               % "3.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-15"          % "3.2.11.0",
    "org.mockito"          %% "mockito-scala"            % "1.17.27",
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "com.vladsch.flexmark" %  "flexmark-all"             % "0.62.2" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
