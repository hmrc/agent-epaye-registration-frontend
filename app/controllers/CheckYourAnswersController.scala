/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{NormalMode, RegistrationRequest, YourBusinessAddress}
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.AgentEpayeRegistrationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.CheckYourAnswersHelper
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            sessionRepository: SessionRepository,
                                            navigator: Navigator,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            agentEpayeRegistrationService: AgentEpayeRegistrationService,
                                            view: CheckYourAnswersView
                                          ) extends FrontendBaseController with I18nSupport with CheckYourAnswersHelper {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val list = SummaryListViewModel(
        rows = makeSummary()
      )

      Ok(view(list))
  }

  def submit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val agentName = request.userAnswers.get(YourAgentNamePage).get
      val contactName = request.userAnswers.get(YourContactDetailsPage).map(_.contactName).getOrElse("")
      val emailAddress =  request.userAnswers.get(YourContactDetailsPage).flatMap(_.emailAddress)
      val telephoneNumber =  request.userAnswers.get(YourContactDetailsPage).flatMap(_.telephoneNumber)
      val addressLine1 =  request.userAnswers.get(YourBusinessAddressPage).map(_.addressLine1).getOrElse("")
      val addressLine2 =  request.userAnswers.get(YourBusinessAddressPage).map(_.addressLine2).getOrElse("")
      val addressLine3 =  request.userAnswers.get(YourBusinessAddressPage).flatMap(_.addressLine3)
      val addressLine4 =  request.userAnswers.get(YourBusinessAddressPage).flatMap(_.addressLine4)
      val postCode =  request.userAnswers.get(YourBusinessAddressPage).map(_.postCode).getOrElse("").replace(" ", "")


      for {
        agentRef <-
          agentEpayeRegistrationService.register(
            RegistrationRequest(
              agentName,
              contactName,
              emailAddress,
              telephoneNumber,
              YourBusinessAddress(
                addressLine1,
                addressLine2,
                addressLine3,
                addressLine4,
                postCode
              )
            )
          )
        updatedAnswers <- Future.fromTry(request.userAnswers.set(PayeAgentReferencePage, agentRef))
        _ <- sessionRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode, updatedAnswers))
  }

}
