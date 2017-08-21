package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import akka.util.Timeout
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, stubFor, urlEqualTo}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.BadGatewayException

class AgentEpayeRegistrationControllerISpec extends BaseControllerISpec {
  private lazy val controller: AgentEpayeRegistrationController = app.injector.instanceOf[AgentEpayeRegistrationController]

  "root context displays start page" in {
    val result = controller.root(FakeRequest())

    status(result) shouldBe 200
    contentAsString(result) should include ("Get an Agent Reference for PAYE for Agents")
  }

  "showRegistrationForm shows the registration form page" in {
    val result = await(controller.showRegistrationForm(FakeRequest()))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("registration.title"))
  }


  "details shows the registrationNameRequest form again if validation fails" when {
    "the agent name is missing" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        registrationNameRequestForm.map {
          case ("agentName", _) => ("agentName", "")
          case x => x
        }: _*
      )

      val result = await(controller.details(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.required"))
    }
  }

  "register shows the registration form again if validation fails" when {

    "the contact name is missing" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        validFormRegestrationDetails.map {
          case ("contactName", _) => ("contactName", "")
          case x => x
        }: _*
      )

      val result = await(controller.details(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.required"))
    }

    "the address line 1 is missing" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        validFormRegestrationDetails.map {
          case ("address.addressLine1", _) => ("address.addressLine1", "")
          case x => x
        } : _*
      )

      val result = await(controller.register(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("registration.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.required"))
    }

    "the contact name is invalid and the telephone number is invalid" in {
      val request = FakeRequest().withFormUrlEncodedBody(
        validFormRegestrationDetails.map {
          case ("contactName", _) => ("contactName", "7^$£")
          case ("telephoneNumber", _) => ("telephoneNumber", "7^$£")
          case x => x
        } : _*
      )

      val result = await(controller.register(request))

      checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.title"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.name.invalid"))
      checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.telephone.invalid"))
    }
  }

  "register shows the registration confirmation page if validation passes" in {
    val agentRef = "HX2000"

    stubFor(post(urlEqualTo(s"/agent-epaye-registration/registrations"))
      .willReturn(
        aResponse()
          .withStatus(Status.OK)
          .withBody(Json.obj("payeAgentReference" -> agentRef).toString())))

    val request = FakeRequest().withFormUrlEncodedBody(validFormRegestrationDetails: _*)
    val result = await(controller.register(request))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("registrationConfirmation.title"))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage(agentRef))
  }

  "register throws exception if there is a problem with the backend service" in {
    val request = FakeRequest().withFormUrlEncodedBody(validFormRegestrationDetails: _*)

    stopServer()

    intercept[BadGatewayException] {
      await(controller.register(request))
    }
  }

  val validFormRegestrationDetails = Seq(
    "contactName" -> "Charlie Contact",
    "telephoneNumber" -> "01234567890",
    "faxNumber" -> "01234567891",
    "emailAddress" -> "foo@bar.com",
    "address.addressLine1" -> "1 Streety Street",
    "address.addressLine2" -> "Towny town",
    "address.addressLine3" -> "",
    "address.addressLine4" -> "",
    "address.postcode" -> "AA111AA"
  )

  val registrationNameRequestForm = Seq(
    "agentName" -> "Angela Agent"
  )


}
