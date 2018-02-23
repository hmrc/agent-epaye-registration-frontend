/*
 * Copyright 2018 HM Revenue & Customs
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

import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.AgentEpayeRegistrationController
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.addressDetails_Scope0.addressDetails_Scope1.addressDetails
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.agentDetails_Scope0.agentDetails_Scope1.agentDetails
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.contactDetails_Scope0.contactDetails_Scope1.contactDetails
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.error_template_Scope0.error_template_Scope1.error_template
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.govuk_wrapper_Scope0.govuk_wrapper_Scope1.govuk_wrapper
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.main_template_Scope0.main_template_Scope1.main_template
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.registration_confirmation_Scope0.registration_confirmation_Scope1.registration_confirmation
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.start_Scope0.start_Scope1.start
import uk.gov.hmrc.agentepayeregistrationfrontend.views.html.summary_Scope0.summary_Scope1.summary
import uk.gov.hmrc.play.test.UnitSpec

class ViewsSpec extends UnitSpec with GuiceOneAppPerTest {

  private val filledForm = AgentEpayeRegistrationController.registrationRequestForm
    .copy(data = Map(
      ("contactName" -> "My contact name"),
      ("agentName" -> "My agent name"),
      ("telephoneNumber" -> "9876543210"),
      ("emailAddress" -> "my@email.com"),
      ("address.addressLine1" -> "My address line 1"),
      ("address.addressLine2" -> "My address line 2"),
      ("address.addressLine3" -> "My address line 3"),
      ("address.addressLine4" -> "My address line 4"),
      ("address.postcode" -> "PO111ST")))

  "error_template view" should {
    "render title, heading and message" in new App {
      val pageTitle = "My custom page title"
      val heading = "My custom heading"
      val message = "My custom message"
      val html = new error_template().render(
        pageTitle = pageTitle,
        heading = heading,
        message = message,
        messages = Messages.Implicits.applicationMessages,
        configuration = app.configuration)
      val content = contentAsString(html)
      content should include(pageTitle)
      content should include(heading)
      content should include(message)

      val html2 = new error_template().f(pageTitle, heading, message)(Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "start view" should {
    "render title and messages" in new App {
      val html = new start().render(
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)
      val content = contentAsString(html)

      import Messages.Implicits.applicationMessages
      content should include(Messages("start.title"))
      content should include(Messages("start.label"))
      content should include(Messages("start.intro"))
      content should include(Messages("start.helpdesklink.text1"))
      content should include(Messages("start.helpdesklink.text2"))

      val html2 = new start().f()(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "agent details view" should {
    "render all of the form field values" in new App {
      val html = new agentDetails().render(
        detailsForm = filledForm,
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)
      val content = contentAsString(html)

      filledForm.data.values.foreach(formValue => content should include(formValue))

      val html2 = new agentDetails().f(filledForm)(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "contact details view" should {
    "render all of the form field values" in new App {
      val html = new contactDetails().render(
        detailsForm = filledForm,
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)
      val content = contentAsString(html)

      filledForm.data.values.foreach(formValue => content should include(formValue))

      val html2 = new contactDetails().f(filledForm)(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "address details view" should {
    "render all of the form field values" in new App {
      val html = new addressDetails().render(
        detailsForm = filledForm,
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)
      val content = contentAsString(html)

      filledForm.data.values.foreach(formValue => content should include(formValue))

      val html2 = new addressDetails().f(filledForm)(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "summary view" should {
    "render all of the form field values" in new App {
      val html = new summary().render(
        detailsForm = filledForm,
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)
      val content = contentAsString(html)

      filledForm.data.values.foreach(formValue => content should include(formValue))

      val html2 = new summary().f(filledForm)(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "registration_confirmation view" should {
    "render the agent reference code" in new App {
      val html = new registration_confirmation().render(
        payeAgentReference = "My custom agent reference",
        request = FakeRequest(),
        messages = Messages.Implicits.applicationMessages,
        config = app.configuration)

      val content = contentAsString(html)

      content should include("My custom agent reference")

      val html2 = new registration_confirmation().f(
        "My custom agent reference")(FakeRequest(), Messages.Implicits.applicationMessages, app.configuration)

      val content2 = contentAsString(html2)
      contentAsString(html2) shouldBe (content)
    }
  }

  "main_template view" should {
    "render all supplied arguments" in new App {
      val view = new main_template()
      val html = view.render(
        title = "My custom page title",
        sidebarLinks = Some(Html("My custom sidebar links")),
        contentHeader = Some(Html("My custom content header")),
        bodyClasses = Some("my-custom-body-class"),
        mainClass = Some("my-custom-main-class"),
        scriptElem = Some(Html("My custom script")),
        mainContent = Html("My custom main content HTML"),
        messages = Messages.Implicits.applicationMessages,
        request = FakeRequest(),
        configuration = app.configuration)

      val content = contentAsString(html)
      content should include("My custom page title")
      content should include("My custom sidebar links")
      content should include("My custom content header")
      content should include("my-custom-body-class")
      content should include("my-custom-main-class")
      content should include("My custom script")
      content should include("My custom main content HTML")

      val html2 = view.f(
        "My custom page title",
        Some(Html("My custom sidebar links")),
        Some(Html("My custom content header")),
        Some("my-custom-body-class"),
        Some("my-custom-main-class"),
        Some(Html("My custom script")))(Html("My custom main content HTML"))(Messages.Implicits.applicationMessages, FakeRequest(), app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }

  "govuk wrapper view" should {
    "render all of the supplied arguments" in new App {

      val html = new govuk_wrapper().render(
        title = "My custom page title",
        mainClass = Some("my-custom-main-class"),
        mainDataAttributes = Some(Html("myCustom=\"attributes\"")),
        bodyClasses = Some("my-custom-body-class"),
        sidebar = Html("My custom sidebar"),
        contentHeader = Some(Html("My custom content header")),
        mainContent = Html("My custom main content"),
        serviceInfoContent = Html("My custom service info content"),
        scriptElem = Some(Html("My custom script")),
        gaCode = Seq("My custom GA code"),
        messages = Messages.Implicits.applicationMessages,
        configuration = app.configuration)

      val content = contentAsString(html)
      content should include("My custom page title")
      content should include("my-custom-main-class")
      content should include("myCustom=\"attributes\"")
      content should include("my-custom-body-class")
      content should include("My custom sidebar")
      content should include("My custom content header")
      content should include("My custom main content")
      content should include("My custom service info content")
      content should include("My custom script")

      val html2 = new govuk_wrapper().f(
        "My custom page title",
        Some("my-custom-main-class"),
        Some(Html("myCustom=\"attributes\"")),
        Some("my-custom-body-class"),
        Html("My custom sidebar"),
        Some(Html("My custom content header")),
        Html("My custom main content"),
        Html("My custom service info content"),
        Some(Html("My custom script")),
        Seq("My custom GA code"))(Messages.Implicits.applicationMessages, app.configuration)
      contentAsString(html2) shouldBe (content)
    }
  }
}
