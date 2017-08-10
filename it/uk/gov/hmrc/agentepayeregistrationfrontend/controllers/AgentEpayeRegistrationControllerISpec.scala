package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import play.api.test.FakeRequest

class AgentEpayeRegistrationControllerISpec extends BaseControllerISpec {
  private lazy val controller: AgentEpayeRegistrationController = app.injector.instanceOf[AgentEpayeRegistrationController]

  "showRegistrationForm shows the registration form page" in {
    val result = await(controller.showRegistrationForm(FakeRequest()))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("registration.title"))
  }

  "register shows the registration form again if validation fails" when {
    "the contact name is missing" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        validFormDetails.map {
          case ("contactName", _) => ("contactName", "")
          case x => x
        } : _*
      )

      val result = await(controller.register(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("registration.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.required"))
    }

    "the contact name is invalid and the telephone number is invalid" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        validFormDetails.map {
          case ("contactName", _) => ("contactName", "7^$£")
          case ("telephoneNumber", _) => ("telephoneNumber", "7^$£")
          case x => x
        } : _*
      )

      val result = await(controller.register(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("registration.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.name.invalid"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.telephone.invalid"))
    }
  }

  "register shows the registration confirmation page if validation passes" in {
    val request = FakeRequest().withFormUrlEncodedBody(validFormDetails: _*)
    val result = await(controller.register(request))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("registrationConfirmation.title"))

    val expectedAgentReference = "HX2000"
    checkHtmlResultWithBodyText(result, htmlEscapedMessage(expectedAgentReference))
  }

  val validFormDetails = Seq(
    "contactName" -> "Charlie Contact",
    "agentName" -> "Angela Agent",
    "telephoneNumber" -> "01234567890",
    "faxNumber" -> "01234567891",
    "emailAddress" -> "foo@bar.com",
    "addressLine1" -> "1 Streety Street",
    "addressLine2" -> "Towny town",
    "addressLine3" -> "",
    "addressLine4" -> "",
    "postcode" -> "AA111AA"
  )
}
