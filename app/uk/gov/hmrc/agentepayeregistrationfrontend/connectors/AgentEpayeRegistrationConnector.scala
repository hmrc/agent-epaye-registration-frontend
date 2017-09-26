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

package uk.gov.hmrc.agentepayeregistrationfrontend.connectors

import java.net.URL
import javax.inject.{Inject, Named, Singleton}

import com.codahale.metrics.MetricRegistry
import com.kenshoo.play.metrics.Metrics
import play.api.libs.json.JsValue
import uk.gov.hmrc.agentepayeregistrationfrontend.models.RegistrationRequest
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost}
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails
import uk.gov.hmrc.agent.kenshoo.monitoring.HttpAPIMonitor

import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationConnector @Inject() (@Named("agent-epaye-registration-baseUrl") baseUrl: URL,
                                                 http: HttpGet with HttpPost,
                                                 metrics: Metrics) extends HttpAPIMonitor {

  override val kenshooRegistry: MetricRegistry = metrics.defaultRegistry

  private val registrationUrl = new URL(baseUrl, s"/agent-epaye-registration/registrations")

  def register(request: RegistrationRequest)(implicit hc: HeaderCarrier): Future[PayeAgentReference] = {
    monitor(s"ConsumedAPI-Agent-Epaye-Registration-POST") {
      http.POST[RegistrationRequest, JsValue](registrationUrl.toString, request).map { json =>
        (json \ "payeAgentReference").as[PayeAgentReference]
      }
    }
  }
}
