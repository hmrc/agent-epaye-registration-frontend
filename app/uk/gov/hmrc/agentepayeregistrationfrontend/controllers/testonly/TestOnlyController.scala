/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.{ Inject, Singleton }
import play.api.http.HttpEntity
import play.api.libs.ws.{ WSClient, WSRequest }
import play.api.mvc._
import play.api.{ Configuration, Environment, Logger, Mode }
import uk.gov.hmrc.agentepayeregistrationfrontend.config.AppConfig
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TestOnlyController @Inject() (
  appConfig: AppConfig,
  val authConnector: AuthConnector,
  val env: Environment,
  ws: WSClient,
  mcc: MessagesControllerComponents)(implicit val config: Configuration)
  extends FrontendController(mcc) with AuthorisedFunctions with AuthRedirects {

  // This implementation is only for test/demo purpose.
  // The actual implementation will be done in EMAC Helpdesk Tool
  val extract: Action[AnyContent] = Action.async {
    implicit request =>
      {
        authorised(Enrolment(appConfig.enrolment) and AuthProviders(PrivilegedApplication)) {
          proxyPassTo(s"${appConfig.opraUrl}/agent-epaye-registration/registrations")
        }.recoverWith {
          case _: NoActiveSession =>
            Future.successful(toStrideLogin(if (env.mode.equals(Mode.Dev)) s"http://${request.host}${request.uri}" else s"${request.uri}"))
          case _: InsufficientEnrolments =>
            Future.successful(Forbidden)
        }
      }
  }

  /**
   * Passes request to the upstream service with additional headers (if any) and returns response downstream as-is
   */
  def proxyPassTo[A](url: String)(implicit request: Request[A], hc: HeaderCarrier): Future[Result] = {
    val requestContentType: Seq[(String, String)] = request.headers.get("Content-Type").map(("Content-Type", _)).toSeq
    val upstreamRequest: WSRequest = ws.url(url)
      .withMethod(request.method)
      .withQueryStringParameters(request.queryString.toSeq.flatMap({ case (k, sv) => sv.map(v => (k, v)) }): _*)
      .withHttpHeaders(hc.withExtraHeaders(requestContentType: _*).headers: _*)
    Logger("TestOnlyController").warn("Sending upstream proxy request: " + upstreamRequest.toString)
    upstreamRequest.stream()
      .map { response =>
        val responseContentType = response.headers.get("Content-Type").flatMap(_.headOption)
        val length = response.headers.get("Content-Length").flatMap(_.headOption).map(_.toLong)

        Results.Status(response.status).sendEntity(HttpEntity.Streamed(response.bodyAsSource, length, responseContentType))
          .withHeaders(response.headers.toSeq.flatMap({ case (k, sv) => sv.map(v => (k, v)) }): _*)
      }
  }

}
