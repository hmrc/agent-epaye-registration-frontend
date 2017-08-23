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

import play.api.Logger
import play.api.Configuration
import play.api.data._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{Address, _}
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.FieldMappings.{addressLine34, _}
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationController @Inject()(override val messagesApi: MessagesApi,
                                                 registrationService: AgentEpayeRegistrationService)(implicit config: Configuration) extends FrontendController with I18nSupport {
  import AgentEpayeRegistrationController._

  val root: Action[AnyContent] = Action { implicit request =>
    Ok(html.start())
  }

  def showAgentDetailsForm = Action { implicit request =>
    Ok(html.agentDetails(registrationRequestForm))
  }

  val agentDetails = Action.async { implicit request =>
    agentDetailsForm.bindFromRequest().fold(
          formWithErrors => {
            Future.successful(Ok(html.agentDetails(formWithErrors)))
          },
          detailsRegistration => {
            Future.successful(Redirect(routes.AgentEpayeRegistrationController.showContactDetailsForm(agentDetailsForm)))
          })
  }

  //registrationService.register(registration).map(x => Ok(html.registration_confirmation(x.value)))

  def showContactDetailsForm(form: Form[RegistrationRequest]) = Action { implicit request =>
    Ok(html.contactDetails(form))
  }

  val contactDetails = Action.async { implicit request =>
    contactDetailsForm.bindFromRequest().fold(
          formWithErrors => {
            Future.successful(Ok(html.contactDetails(contactDetailsForm)))
          },
          detailsRegistration => {
            //Future.successful(Ok(html.addressDetails(contactDetailsForm)))
            Future.successful(Ok)
          })
      }



  def showAddressDetailsForm = Action { implicit request =>
    //Ok(html.addressDetails(registrationRequestForm))
    Ok
  }
}

object AgentEpayeRegistrationController {
  val agentDetailsForm = Form(
    mapping(
      "agentName" -> name,
      "contactName" -> text,
      "telephoneNumber" -> optional(text),
      "faxNumber" -> optional(text),
      "emailAddress" -> optional(text),
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text
      )(Address.apply)(Address.unapply)
    )(RegistrationRequest.apply)(RegistrationRequest.unapply)
  )

  val contactDetailsForm = Form(
    mapping(
      "agentName" -> text,
      "contactName" -> name,
      "telephoneNumber" -> telephone,
      "faxNumber" -> optional(text),
      "emailAddress" -> optional(email),
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text
      )(Address.apply)(Address.unapply)
    )(RegistrationRequest.apply)(RegistrationRequest.unapply)
  )

  val addressDetailsForm = Form(
    mapping(
      "agentName" -> text,
      "contactName" -> text,
      "telephoneNumber" -> optional(text),
      "faxNumber" -> optional(text),
      "emailAddress" -> optional(email),
      "address" -> mapping(
        "addressLine1" -> addressLine12,
        "addressLine2" -> addressLine12,
        "addressLine3" -> addressLine34,
        "addressLine4" -> addressLine34,
        "postcode" -> postcode
      )(Address.apply)(Address.unapply)
    )(RegistrationRequest.apply)(RegistrationRequest.unapply)
  )

  val registrationRequestForm = Form[RegistrationRequest](
    mapping(
      "agentName" -> name,
      "contactName" -> text,
      "telephoneNumber" -> optional(text),
      "faxNumber" -> optional(text),
      "emailAddress" -> optional(text),
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text
      )(Address.apply)(Address.unapply)
    )(RegistrationRequest.apply)(RegistrationRequest.unapply)
  )
}