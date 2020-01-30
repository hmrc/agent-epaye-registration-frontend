import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.ForkedJvmPerTestSettings

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val compileDeps = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-play-26" % "1.3.0",
  "uk.gov.hmrc" %% "govuk-template" % "5.48.0-play-26",
  "uk.gov.hmrc" %% "play-ui" % "8.7.0-play-26",
  "uk.gov.hmrc" %% "auth-client" % "2.32.2-play-26",
  "uk.gov.hmrc" %% "play-partials" % "6.9.0-play-26",
  "uk.gov.hmrc" %% "agent-kenshoo-monitoring" % "4.0.0",
  "uk.gov.hmrc" %% "agent-mtd-identifiers" % "0.17.0-play-26",
  "uk.gov.hmrc" %% "emailaddress" % "3.4.0"
)

def testDeps(scope: String) = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.9.0-play-26" % scope,
  "org.scalatest" %% "scalatest" % "3.0.8" % scope,
  "org.mockito" % "mockito-core" % "3.2.4" % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % scope,
  "com.github.tomakehurst" % "wiremock" % "2.26.0" % scope
)

val jettyVersion = "9.2.24.v20180105"

lazy val root = (project in file("."))
  .settings(
    name := "agent-epaye-registration-frontend",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.12.10",
    PlayKeys.playDefaultPort := 9446,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= compileDeps ++ testDeps("test") ++ testDeps("it"),
    dependencyOverrides ++= Seq(
      "org.eclipse.jetty" % "jetty-server" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-servlet" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-security" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-servlets" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-continuation" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-xml" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-client" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-http" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-io" % jettyVersion % "it",
      "org.eclipse.jetty" % "jetty-util" % jettyVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-api" % jettyVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-common" % jettyVersion % "it",
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyVersion % "it"
    ),
    publishingSettings,
    scoverageSettings,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .configs(IntegrationTest)
  .settings(
    Keys.fork in IntegrationTest := false,
    Defaults.itSettings,
    unmanagedSourceDirectories in IntegrationTest += baseDirectory(_ / "it").value,
    parallelExecution in IntegrationTest := false,
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value)
  )
  .settings(scalariformItSettings, majorVersion := 0)
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = ForkedJvmPerTestSettings.oneForkedJvmPerTest(tests)
