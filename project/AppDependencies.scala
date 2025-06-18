import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.13.0"
  val mongoVersion     = "2.6.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "12.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"       %% "agent-mtd-identifiers"      % "2.2.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoVersion,
    "org.scalatestplus" %% "scalacheck-1-15"         % "3.2.11.0",
    "org.mockito"       %% "mockito-scala"           % "1.17.27",
    "com.vladsch.flexmark" % "flexmark-all" % "0.62.2" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
