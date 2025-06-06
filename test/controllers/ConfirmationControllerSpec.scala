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

import base.SpecBase
import models.UserAnswers
import pages.PayeAgentReferencePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.domain.PayeAgentReference
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBase {

  "Confirmation Controller" - {

    "must redirect to the IndexController when there is no 'PayeAgentReferencePage' held in the session" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must return OK and the correct view for a GET when there is a 'PayeAgentReferencePage' held in the session" in {
      val agentReference = PayeAgentReference("AB1234")

      val userAnswers = UserAnswers(userAnswersId).set(PayeAgentReferencePage, agentReference).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConfirmationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(agentReference)(request, messages(application)).toString
      }
    }

  }

}
