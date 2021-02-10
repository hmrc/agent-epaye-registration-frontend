/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.agentepayeregistrationfrontend.config.AppConfig
import uk.gov.hmrc.agentepayeregistrationfrontend.models.RegistrationRequest
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.http.HttpReadsInstances._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class AgentEpayeRegistrationConnector @Inject() (
  config: AppConfig,
  http: HttpClient,
  implicit val ec: ExecutionContext) {

  private val registrationUrl = config.opraUrl + "/agent-epaye-registration/registrations"

  def register(request: RegistrationRequest)(implicit hc: HeaderCarrier): Future[PayeAgentReference] = {
    http.POST[RegistrationRequest, HttpResponse](registrationUrl, request).map {
      case response if response.status >= 400 && response.status <= 599 =>
        throw UpstreamErrorResponse("[AgentEpayeRegistrationConnector][register]: Failed POST of registration request", response.status)
      case response => (response.json \ "payeAgentReference").as[PayeAgentReference]
    }
  }
}
