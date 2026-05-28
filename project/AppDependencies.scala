import sbt.*

object AppDependencies {

  val bootstrapVersion = "10.7.0"
  val mongoVersion     = "2.12.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "13.6.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"      % "3.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoVersion,
    "org.scalatestplus" %% "scalacheck-1-15"         % "3.2.11.0",
    "org.mockito"       %% "mockito-scala"           % "2.2.1",
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
