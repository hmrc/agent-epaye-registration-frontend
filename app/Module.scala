/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.net.{InetSocketAddress, URL}
import java.util.concurrent.TimeUnit.{MILLISECONDS, SECONDS}
import javax.inject.{Inject, Provider, Singleton}

import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.{MetricFilter, SharedMetricRegistries}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import org.slf4j.MDC
import play.api.Mode.Mode
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment, Logger}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HttpGet, HttpPost}
import wiring.WSVerbs

import scala.concurrent.{ExecutionContext, Future}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ServicesConfig {


  override protected lazy val mode: Mode = environment.mode
  override protected lazy val runModeConfiguration: Configuration = configuration

  def configure(): Unit = {
    lazy val appName = configuration.getString("appName").get
    lazy val loggerDateFormat: Option[String] = configuration.getString("logger.json.dateformat")

    Logger.info(s"Starting microservice : $appName : in mode : ${environment.mode}")
    MDC.put("appName", appName)
    loggerDateFormat.foreach(str => MDC.put("logger.json.dateformat", str))


    bind(classOf[HttpGet]).toInstance(new WSVerbs)
    bind(classOf[HttpPost]).toInstance(new WSVerbs)
    bindBaseUrl("agent-epaye-registration")
    bind(classOf[GraphiteStartUp]).asEagerSingleton()
  }

  private def bindBaseUrl(serviceName: String) =
    bind(classOf[URL]).annotatedWith(Names.named(s"$serviceName-baseUrl")).toProvider(new BaseUrlProvider(serviceName))

  private class BaseUrlProvider(serviceName: String) extends Provider[URL] {
    override lazy val get = new URL(baseUrl(serviceName))
  }
}

@Singleton
class GraphiteStartUp @Inject()(configuration: Configuration,
                                lifecycle: ApplicationLifecycle,
                                implicit val ec: ExecutionContext) {

  val metricsPluginEnabled: Boolean = configuration.getBoolean("metrics.enabled").getOrElse(false)

  val graphitePublisherEnabled: Boolean = configuration.getBoolean("microservice.metrics.graphite.enabled").getOrElse(false)

  val graphiteEnabled: Boolean = metricsPluginEnabled && graphitePublisherEnabled

  val registryName: String = configuration.getString("metrics.name").getOrElse("default")

  val graphite = new Graphite(new InetSocketAddress(
    configuration.getString("graphite.host").getOrElse("graphite"),
    configuration.getInt("graphite.port").getOrElse(2003)))

  val prefix: String = configuration.getString("graphite.prefix").
    getOrElse(s"tax.${configuration.getString("appName")}")

  val reporter: GraphiteReporter = GraphiteReporter.forRegistry(
    SharedMetricRegistries.getOrCreate(registryName))
    .prefixedWith(s"$prefix.${java.net.InetAddress.getLocalHost.getHostName}")
    .convertRatesTo(SECONDS)
    .convertDurationsTo(MILLISECONDS)
    .filter(MetricFilter.ALL)
    .build(graphite)

  private def startGraphite() {
    Logger.info("Graphite metrics enabled, starting the reporter")
    reporter.start(configuration.getLong("graphite.interval").getOrElse(10L), SECONDS)
  }

  if (graphiteEnabled) startGraphite()
  lifecycle.addStopHook { () =>
    Future successful reporter.stop()
  }
}