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

package uk.gov.hmrc.agentepayeregistrationfrontend.views

import org.scalatestplus.play.MixedPlaySpec
import play.api.data.Form
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.AgentEpayeRegistrationController
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.error_template_Scope0.error_template_Scope1.error_template
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.main_template_Scope0.main_template_Scope1.main_template
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.registration_Scope0.registration_Scope1.registration
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.registration_confirmation_Scope0.registration_confirmation_Scope1.registration_confirmation

class ViewsSpec extends MixedPlaySpec {

  "error_template view" should {

    "render title, heading and message" in new App {
      val html = new error_template().render(
        "My custom page title","My custom heading","My custom message",
        Messages.Implicits.applicationMessages, app.configuration )
      val content = contentAsString(html)
      content must include ("My custom page title")
      content must include ("My custom heading")
      content must include ("My custom message")
    }
  }

  "registration view" should {
    "render all of the form field values" in new App {
      val form = AgentEpayeRegistrationController.registrationRequestForm
        .copy(data = Map(
          ("contactName" -> "My contact name"),
          ("agentName" -> "My agent name"),
          ("telephoneNumber" -> "9876543210"),
          ("faxNumber" -> "0123456789"),
          ("emailAddress" -> "my@email.com"),
          ("address.addressLine1" -> "My address line 1"),
          ("address.addressLine2" -> "My address line 2"),
          ("address.addressLine3" -> "My address line 3"),
          ("address.addressLine4" -> "My address line 4"),
          ("address.postcode" -> "PO111ST")
        ))

      val html = new registration().render(form, FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      val content = contentAsString(html)

      form.data.values.foreach(formValue => content must include (formValue))
    }
  }

  "registration_confirmation view" should {
    "render the agent reference code" in new App {
      val html = new registration_confirmation().render("My custom agent reference",
        FakeRequest(),
        Messages.Implicits.applicationMessages,
        app.configuration)

      contentAsString(html) must include ("My custom agent reference")
    }
  }

  "main_template view" should {
    "render all supplied arguments" in new App {
      val html = new main_template().render(
        "My custom page title",
        Some(Html("My custom sidebar links")),
        Some(Html("My custom content header")),
        Some("my-custom-body-class"),
        Some("my-custom-main-class"),
        Some(Html("My custom script")),
        Html("My custom main content HTML"),
        Messages.Implicits.applicationMessages, FakeRequest(), app.configuration )
      val content = contentAsString(html)
      content must include ("My custom page title")
      content must include ("My custom sidebar links")
      content must include ("My custom content header")
      content must include ("my-custom-body-class")
      content must include ("my-custom-main-class")
      content must include ("My custom script")
      content must include ("My custom main content HTML")
    }
  }
}
