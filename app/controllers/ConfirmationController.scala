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

package controllers

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.PayeAgentReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.CheckYourAnswersHelper
import views.html.ConfirmationView

class ConfirmationController @Inject() (
    override val messagesApi: MessagesApi,
    identify: IdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    val controllerComponents: MessagesControllerComponents,
    view: ConfirmationView
) extends FrontendBaseController
    with I18nSupport
    with CheckYourAnswersHelper {

  def onPageLoad(): Action[AnyContent] = identify.andThen(getData).andThen(requireData) { implicit request =>
    request.userAnswers.get(PayeAgentReferencePage) match {
      case Some(ref) => Ok(view(ref))
      case None      => Redirect(routes.IndexController.onPageLoad)
    }
  }

}
