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

import javax.inject.{Inject, Named, Singleton}

import play.api.{Configuration, Environment, Mode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.agentepayeregistrationfrontend.connectors.AuthConnector
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html
import uk.gov.hmrc.auth.core.authorise.Enrolment
import uk.gov.hmrc.auth.core.retrieve.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.retrieve.AuthProviders
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, InsufficientEnrolments, NoActiveSession}
import uk.gov.hmrc.auth.frontend.Redirects
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class TestOnlyController @Inject()(@Named("extract.auth.stride.enrolment") strideEnrolment: String,
                                    override val messagesApi: MessagesApi,
                                    val authConnector: AuthConnector,
                                    registrationService: AgentEpayeRegistrationService,
                                    val env: Environment)
                                  (implicit val config: Configuration)
  extends FrontendController with I18nSupport with AuthorisedFunctions with Redirects {

  //TODO This implementation is only for test/demo purpose. The actual implementation will be done along the completion of APB-1125
  val extract: Action[AnyContent] = Action.async {
    implicit request => {
      authorised(Enrolment(strideEnrolment) and AuthProviders(PrivilegedApplication)) {
        registrationService.extract.map { _ =>
          Ok(html.start())
        }
      }.recoverWith {
        case _: NoActiveSession =>
          Future.successful(toStrideLogin(if (env.mode.equals(Mode.Dev)) s"http://${request.host}${request.uri}" else s"${request.uri}"))
        case _: InsufficientEnrolments =>
          Future.successful(Forbidden)
      }
    }
  }

}
