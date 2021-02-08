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

package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import javax.inject.{ Inject, Singleton }
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents }
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.FieldMappings._
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{ Address, PageID, RegistrationRequest }
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.{ start, agentDetails, contactDetails, addressDetails, summary, registration_confirmation }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AgentEpayeRegistrationController @Inject() (
  registrationService: AgentEpayeRegistrationService,
  startView: start,
  agentDetailsView: agentDetails,
  contactDetailsView: contactDetails,
  addressDetailsView: addressDetails,
  summaryView: summary,
  registrationConfirmationView: registration_confirmation,
  mcc: MessagesControllerComponents)(implicit config: Configuration) extends FrontendController(mcc) with I18nSupport {
  import AgentEpayeRegistrationController._

  val root: Action[AnyContent] = Action {
    Redirect(routes.AgentEpayeRegistrationController.start().url)
  }

  val start: Action[AnyContent] = Action { implicit request =>
    Ok(startView())
  }

  def showAgentDetailsForm: Action[AnyContent] = Action { implicit request =>
    Ok(agentDetailsView(agentDetailsForm))
  }

  val details: Action[AnyContent] = Action.async { implicit request =>
    pageIdForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest),
      pageIdData => {
        pageIdData.pageId match {
          case "agentDetails" => agentDetailsForm.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(Ok(agentDetailsView(formWithErrors)))
            },
            data => {
              Future.successful(Ok(contactDetailsView(agentDetailsForm.fill(data))))
            })
          case "contactDetails" => contactDetailsForm.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(Ok(contactDetailsView(formWithErrors)))
            },
            data => {
              Future.successful(Ok(addressDetailsView(contactDetailsForm.fill(data))))
            })
          case "addressDetails" => registrationRequestForm.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(Ok(addressDetailsView(formWithErrors)))
            },
            data => {
              Future.successful(Ok(summaryView(registrationRequestForm.fill(data))))
            })
          case "confirmationPage" => registrationRequestForm.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(Ok(summaryView(formWithErrors)))
            },
            data => {
              Form(single("amend" -> text)).bindFromRequest().fold(
                _ => {
                  registrationService
                    .register(withNoSpacesInPostCode(data))
                    .map(agentRef => {
                      Redirect(routes.AgentEpayeRegistrationController.confirmation().url)
                        .addingToSession(sessionKeyAgentRef -> agentRef.value)
                    })
                },
                amend => {
                  val filledForm = registrationRequestForm.fill(data)
                  amend match {
                    case "agentDetails" => Future.successful(Ok(agentDetailsView(filledForm)))
                    case "contactDetails" => Future.successful(Ok(contactDetailsView(filledForm)))
                    case "addressDetails" => Future.successful(Ok(addressDetailsView(filledForm)))
                  }
                })
            })
          case _ => Future.successful(BadRequest)
        }
      })
  }

  val confirmation: Action[AnyContent] = Action { implicit request =>
    request.session.get(sessionKeyAgentRef).map { agentRef =>
      Ok(registrationConfirmationView(agentRef))
    }.getOrElse(BadRequest)
  }

  private def withNoSpacesInPostCode(data: RegistrationRequest): RegistrationRequest =
    data.copy(address = data.address.copy(postCode = data.address.withoutSpacesInPostCode))
}

object AgentEpayeRegistrationController {
  private[controllers] val sessionKeyAgentRef = "agentRef"

  val agentDetailsForm: Form[RegistrationRequest] = Form(
    mapping(
      "agentName" -> name,
      "contactName" -> text,
      "emailAddress" -> optional(text),
      "telephoneNumber" -> optional(text),
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))

  val contactDetailsForm: Form[RegistrationRequest] = Form(
    mapping(
      "agentName" -> name,
      "contactName" -> contactName,
      "emailAddress" -> emailAddr,
      "telephoneNumber" -> telephone,
      "address" -> mapping(
        "addressLine1" -> text,
        "addressLine2" -> text,
        "addressLine3" -> optional(text),
        "addressLine4" -> optional(text),
        "postcode" -> text)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))

  val registrationRequestForm: Form[RegistrationRequest] = Form(
    mapping(
      "agentName" -> name,
      "contactName" -> contactName,
      "emailAddress" -> emailAddr,
      "telephoneNumber" -> telephone,
      "address" -> mapping(
        "addressLine1" -> addressLine1,
        "addressLine2" -> addressLine2,
        "addressLine3" -> addressLine3,
        "addressLine4" -> addressLine4,
        "postcode" -> postcode)(Address.apply)(Address.unapply))(RegistrationRequest.apply)(RegistrationRequest.unapply))

  val pageIdForm: Form[PageID] = Form(
    mapping("pageId" -> text)(PageID.apply)(PageID.unapply))
}
