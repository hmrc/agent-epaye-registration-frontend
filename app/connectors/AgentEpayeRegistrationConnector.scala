/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import config.FrontendAppConfig
import models.RegistrationRequest
import play.api.libs.json.Json
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.http.HttpReadsInstances._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentEpayeRegistrationConnector @Inject()(
                                                 config: FrontendAppConfig,
                                                 http: HttpClientV2,
                                                 implicit val ec: ExecutionContext) {

  private lazy val registrationUrl = config.opraUrl + "/agent-epaye-registration/registrations"

  def register(request: RegistrationRequest)(implicit hc: HeaderCarrier): Future[PayeAgentReference] = {
    http.post(new URL(registrationUrl)).withBody(Json.toJson(request)).execute[HttpResponse].map {
      case response if response.status >= 400 && response.status <= 599 =>
        throw UpstreamErrorResponse("[AgentEpayeRegistrationConnector][register]: Failed POST of registration request", response.status)
      case response => (response.json \ "payeAgentReference").as[PayeAgentReference]
    }
  }
}
