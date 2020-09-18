import sbt.Tests.Group
import uk.gov.hmrc.{ForkedJvmPerTestSettings, SbtAutoBuildPlugin}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

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
  "uk.gov.hmrc"             %% "bootstrap-play-26"          % "1.13.0",
  "uk.gov.hmrc"             %% "govuk-template"             % "5.55.0-play-26",
  "uk.gov.hmrc"             %% "play-ui"                    % "8.12.0-play-26",
  "uk.gov.hmrc"             %% "auth-client"                % "3.0.0-play-26",
  "uk.gov.hmrc"             %% "play-partials"              % "6.11.0-play-26",
  "uk.gov.hmrc"             %% "agent-kenshoo-monitoring"   % "4.4.0",
  "uk.gov.hmrc"             %% "agent-mtd-identifiers"      % "0.17.0-play-26",
  "uk.gov.hmrc"             %% "emailaddress"               % "3.4.0"
)

def testDeps(scope: String) = Seq(
  "uk.gov.hmrc"             %% "hmrctest"                   % "3.9.0-play-26" % scope,
  "org.scalatest"           %% "scalatest"                  % "3.0.8"         % scope,
  "org.mockito"             % "mockito-core"                % "3.3.3"         % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play"         % "3.1.3"         % scope,
  "com.github.tomakehurst"  % "wiremock"                    % "2.26.3"        % scope
)

val jettyVersion = "9.2.24.v20180105"

lazy val root = (project in file("."))
  .settings(
    name := "agent-epaye-registration-frontend",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.12.11",
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
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    scalacOptions += "-P:silencer:lineContentFilters=^\\w",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.0" cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % "1.7.0" % Provided cross CrossVersion.full
    )
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
  .disablePlugins(JUnitXmlReportPlugin)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = ForkedJvmPerTestSettings.oneForkedJvmPerTest(tests)
