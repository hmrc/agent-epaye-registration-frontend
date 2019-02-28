/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.agentepayeregistrationfrontend.connectors

import com.codahale.metrics.MetricRegistry
import com.kenshoo.play.metrics.Metrics
import javax.inject.{ Inject, Singleton }
import play.api.libs.json.JsValue
import uk.gov.hmrc.agent.kenshoo.monitoring.HttpAPIMonitor
import uk.gov.hmrc.agentepayeregistrationfrontend.config.AppConfig
import uk.gov.hmrc.agentepayeregistrationfrontend.models.RegistrationRequest
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails

import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationConnector @Inject() (
  config: AppConfig,
  http: DefaultHttpClient) {

  private val registrationUrl = config.opraUrl + "/agent-epaye-registration/registrations"

  def register(request: RegistrationRequest)(implicit hc: HeaderCarrier): Future[PayeAgentReference] = {
    http.POST[RegistrationRequest, JsValue](registrationUrl, request).map { json =>
      (json \ "payeAgentReference").as[PayeAgentReference]
    }
  }
}
