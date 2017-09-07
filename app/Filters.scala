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

import javax.inject.Inject

import akka.stream.Materializer
import com.kenshoo.play.metrics.MetricsFilter
import play.api.http.DefaultHttpFilters
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.audit.filters.FrontendAuditFilter
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.inject.RunMode
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import uk.gov.hmrc.play.http.logging.filters.FrontendLoggingFilter

import scala.concurrent.ExecutionContext

/**
  * Defines the filters that are added to the application by extending the default Play filters
  *
  * @param loggingFilter - used to log details of any http requests hitting the service
  * @param auditFilter   - used to call the datastream microservice and publish auditing events
  * @param metricsFilter - used to collect metrics and statistics relating to the service
  */
class Filters @Inject()(loggingFilter: LoggingFilter, auditFilter: MicroserviceAuditFilter, metricsFilter: MetricsFilter)
  extends DefaultHttpFilters(loggingFilter, auditFilter, metricsFilter, SessionCookieCryptoFilter)

class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext, configuration: Configuration) extends FrontendLoggingFilter {
  override def controllerNeedsLogging(controllerName: String): Boolean = configuration.getBoolean(s"controllers.$controllerName.needsLogging").getOrElse(true)
}

class MicroserviceAuditFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext,
                                        configuration: Configuration, val auditConnector: MicroserviceAuditConnector) extends FrontendAuditFilter {

  override lazy val maskedFormFields = Seq("password")

  override def controllerNeedsAuditing(controllerName: String): Boolean = configuration.getBoolean(s"controllers.$controllerName.needsAuditing").getOrElse(true)

  override def appName: String = configuration.getString("appName").get

  override def applicationPort: Option[Int] = None
}

class MicroserviceAuditConnector @Inject()(val environment: Environment) extends AuditConnector with RunMode {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}
