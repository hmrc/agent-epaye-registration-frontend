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

import javax.inject.{ Inject, Singleton }

import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.{ Action, AnyContent }
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.FieldMappings._
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{ Address, RegistrationRequest }
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationController @Inject() (
  override val messagesApi: MessagesApi,
  registrationService: AgentEpayeRegistrationService)(implicit config: Configuration) extends FrontendController with I18nSupport {
  import AgentEpayeRegistrationController._

  val root: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.AgentEpayeRegistrationController.start().url)
  }

  val start: Action[AnyContent] = Action { implicit request =>
    Ok(html.start())
  }

  def showAgentDetailsForm = Action { implicit request =>
    Ok(html.agentDetails(agentDetailsForm))
  }

  val agentDetails = Action.async { implicit request =>
    agentDetailsForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(Ok(html.agentDetails(formWithErrors)))
      },
      data => {
        Future.successful(Ok(html.contactDetails(agentDetailsForm.fill(data))))
      })
  }

  val contactDetails = Action.async { implicit request =>
    contactDetailsForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(Ok(html.contactDetails(formWithErrors)))
      },
      data => {
        Future.successful(Ok(html.addressDetails(contactDetailsForm.fill(data))))
      })
  }

  val addressDetails = Action.async { implicit request =>
    registrationRequestForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(Ok(html.addressDetails(formWithErrors)))
      },
      data => {
        Future.successful(Ok(html.summary(registrationRequestForm.fill(data))))
      })
  }

  val summary = Action.async { implicit request =>
    registrationRequestForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(Ok(html.summary(formWithErrors)))
      },
      data => {
        Form(single("amend" -> text)).bindFromRequest().fold(
          _ => registrationService.register(withNoSpacesInPostCode(data)).map(x => Ok(html.registration_confirmation(x.value))),
          amend => {
            val filledForm = registrationRequestForm.fill(data)
            amend match {
              case "agentDetails" => Future.successful(Ok(html.agentDetails(filledForm)))
              case "contactDetails" => Future.successful(Ok(html.contactDetails(filledForm)))
              case "addressDetails" => Future.successful(Ok(html.addressDetails(filledForm)))
            }
          })
      })
  }

  private def withNoSpacesInPostCode(data: RegistrationRequest): RegistrationRequest =
    data.copy(address = data.address.copy(postCode = data.address.withoutSpacesInPostCode))
}

object AgentEpayeRegistrationController {

  val agentDetailsForm = Form[RegistrationRequest](
    mapping(
      "agentName" -> name,
      "contactName" -> text,
      "telephoneNumber" -> optional(text),
      "emailAddress" -> optional(text),
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))

  val contactDetailsForm = Form[RegistrationRequest](
    mapping(
      "agentName" -> name,
      "contactName" -> name,
      "telephoneNumber" -> telephone,
      "emailAddress" -> emailAddr,
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))

  val registrationRequestForm = Form[RegistrationRequest](
    mapping(
      "agentName" -> name,
      "contactName" -> name,
      "telephoneNumber" -> telephone,
      "emailAddress" -> emailAddr,
      "address" -> mapping(
        "addressLine1" -> addressLine12,
        "addressLine2" -> addressLine12,
        "addressLine3" -> addressLine34,
        "addressLine4" -> addressLine34,
        "postcode" -> postcode)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))
}