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

package uk.gov.hmrc.agentepayeregistrationfrontend.controllers.testonly

import java.net.URL
import javax.inject.{Inject, Named, Singleton}

import akka.util.ByteString
import play.api.http.{HttpEntity, Writeable}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.{StreamedResponse, WSClient}
import play.api.mvc._
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.agentepayeregistrationfrontend.connectors.AuthConnector
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.auth.core.authorise.Enrolment
import uk.gov.hmrc.auth.core.retrieve.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.retrieve.AuthProviders
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, InsufficientEnrolments, NoActiveSession}
import uk.gov.hmrc.auth.frontend.Redirects
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class TestOnlyController @Inject()(@Named("extract.auth.stride.enrolment") strideEnrolment: String,
                                   override val messagesApi: MessagesApi,
                                   val authConnector: AuthConnector,
                                   val env: Environment,
                                   ws: WSClient,
                                   @Named("agent-epaye-registration-baseUrl") registrationBaseUrl: URL)
                                  (implicit val config: Configuration)
  extends FrontendController with I18nSupport with AuthorisedFunctions with Redirects {

  // This implementation is only for test/demo purpose.
  // The actual implementation will be done in EMAC Helpdesk Tool
  val extract: Action[RawBuffer] = Action.async(parse.raw) {
    implicit request => {
      authorised(Enrolment(strideEnrolment) and AuthProviders(PrivilegedApplication)) {
        proxyPassTo(s"$registrationBaseUrl/agent-epaye-registration/registrations")
      }.recoverWith {
        case _: NoActiveSession        =>
          Future.successful(toStrideLogin(if (env.mode.equals(Mode.Dev)) s"http://${request.host}${request.uri}" else s"${request.uri}"))
        case _: InsufficientEnrolments =>
          Future.successful(Forbidden)
      }
    }
  }

  /**
    * Passes request to the upstream service with additional headers (if any) and returns response downstream as-is
    */
  def proxyPassTo(url: String)(implicit request: Request[RawBuffer], hc: HeaderCarrier, wr: Writeable[ByteString]): Future[Result] = {
    request.body
    ws.url(url)
      .withMethod(request.method)
      .withQueryString(request.queryString.toSeq.flatMap({ case (k, sv) => sv.map(v => (k, v)) }): _*)
      .withHeaders(hc.withExtraHeaders(request.headers.headers: _*).headers: _*)
      .withBody(request.body.initialData)
      .stream()
      .map {
        case StreamedResponse(response, body) =>
          val contentType = response.headers.get("Content-Type").flatMap(_.headOption)
          val length = response.headers.get("Content-Length").flatMap(_.headOption).map(_.toLong)

          Results.Status(response.status).sendEntity(HttpEntity.Streamed(body, length, contentType))
            .withHeaders(response.headers.toSeq.flatMap({ case (k, sv) => sv.map(v => (k, v)) }): _*)
      }
  }

}
