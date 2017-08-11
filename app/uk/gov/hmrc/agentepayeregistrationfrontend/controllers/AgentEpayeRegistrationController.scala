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

package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{Address, RegistrationRequest}
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.FieldMappings._
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationController @Inject()(override val messagesApi: MessagesApi,
                                                 registrationService: AgentEpayeRegistrationService)(implicit config: Configuration) extends FrontendController with I18nSupport {
  import AgentEpayeRegistrationController._

  val root: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.AgentEpayeRegistrationController.register())
  }

  val showRegistrationForm = Action { implicit request =>
    Ok(html.registration(registrationRequestForm))
  }

  val register = Action.async { implicit request =>
    registrationRequestForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(Ok(html.registration(formWithErrors)))
      },
      registration => {
        registrationService.register(registration).map(x => Ok(html.registration_confirmation(x.value)))
      }
    )
  }
}

object AgentEpayeRegistrationController {
  val registrationRequestForm = Form[RegistrationRequest](
    mapping(
      "agentName" -> name,
      "contactName" -> name,
      "telephoneNumber" -> telephone,
      "faxNumber" -> telephone,
      "emailAddress" -> emailAddr,
      "address" -> mapping(
        "addressLine1" -> addressLine12,
        "addressLine2" -> addressLine12,
        "addressLine3" -> addressLine34,
        "addressLine4" -> addressLine34,
        "postcode" -> postcode
      )(Address.apply)(Address.unapply)
    )(RegistrationRequest.apply)(RegistrationRequest.unapply)
  )
}