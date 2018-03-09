package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.redirectLocation
import uk.gov.hmrc.http.BadGatewayException
import scala.concurrent.duration._

class AgentEpayeRegistrationControllerISpec extends BaseControllerISpec {
  private lazy val controller: AgentEpayeRegistrationController = app.injector.instanceOf[AgentEpayeRegistrationController]

  "root context redirects to start page" in {
    val result = controller.root(FakeRequest())

    status(result) shouldBe 303

    val timeout = 2.seconds
    redirectLocation(result)(timeout).get should include("/start")
  }

  "start displays start page" in {
    val result = controller.start(FakeRequest())

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("start.title"))
  }

  "showAgentDetailsForm displays the agent details form page" in {
    val result = await(controller.showAgentDetailsForm(FakeRequest()))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.agent.label"))
  }

  "POST to /details" when {

    "pageId is missing" should {
      "return BAD_REQUEST" in {
        status(controller.details(FakeRequest())) shouldBe 400
      }
    }

    "pageId is unknown" should {
      "return BAD_REQUEST" in {
        val request = FakeRequest().withFormUrlEncodedBody(validFormRegistrationDetails("pageId" -> "fnords"): _*)

        status(controller.details(request)) shouldBe 400
      }
    }

    "pageId is agentDetails" should {

      "show the agentDetails page with the validation errors if the agent name is missing" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "agentDetails", "agentName" -> ""): _*)

        val result = await(controller.details(request))

        checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.agent.label"))
        checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.agentName.empty"))
      }

      "show the contactDetails page if validation passes" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "agentDetails"): _*)
        val result = await(controller.details(request))

        checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.contact.title"))
      }

      "retain all the form state and pass it to the next page" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "agentDetails"): _*)

        val result = await(controller.details(request))
        checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "contactDetails"))
      }
    }

    "pageId is contactDetails" should {
      "show the contactDetails page with the validation errors" when {
        "the contact name is missing" in {
          val request = FakeRequest().withFormUrlEncodedBody(
            validFormRegistrationDetails("pageId" -> "contactDetails", "contactName" -> ""): _*)

          val result = await(controller.details(request))

          checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.contact.title"))
          checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.contactName.empty"))
        }

        "the contact name is invalid and the telephone number is invalid" in {
          val request = FakeRequest().withFormUrlEncodedBody(
            validFormRegistrationDetails(
              "pageId" -> "contactDetails",
              "contactName" -> "7^$£",
              "telephoneNumber" -> "7^$£"): _*)

          val result = await(controller.details(request))

          checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.contact.title"))
          checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.contactName.invalid"))
          checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.telephone.invalid"))
        }
      }

      "show the addressDetails page if validation passes" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "contactDetails"): _*)

        val result = await(controller.details(request))

        checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.address.title"))
      }

      "retain all the form state and pass it to the next page" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "contactDetails"): _*)

        val result = await(controller.details(request))
        checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "addressDetails"))
      }
    }

    "pageId is addressDetails" should {
      "show the addressDetails page again with the validation errors if address line 1 is missing" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "addressDetails", "address.addressLine1" -> ""): _*)

        val result = await(controller.details(request))

        checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.address.title"))
        checkHtmlResultWithBodyText(result, htmlEscapedMessage("error.addressLine1.empty"))
      }

      "show the summary page if validation passes" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "addressDetails"): _*)

        val result = await(controller.details(request))
        checkHtmlResultWithBodyText(result, htmlEscapedMessage("summary.title"))
      }

      "retain all the form state and pass it to the next page" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          validFormRegistrationDetails("pageId" -> "addressDetails"): _*)

        val result = await(controller.details(request))
        checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "confirmationPage"))
      }
    }

    "pageId is summary" should {
      "show the registration confirmation page if validation passes" in {
        val agentRef = "HX2000"

        stubFor(post(urlEqualTo("/agent-epaye-registration/registrations"))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(Json.obj("payeAgentReference" -> agentRef).toString())))

        val request = FakeRequest().withFormUrlEncodedBody(validFormRegistrationDetails("pageId" -> "confirmationPage"): _*)

        val result = await(controller.details(request))

        status(result) shouldBe 303

        result.header.headers("Location") should include("/agent-epaye-registration/confirmation")
        result.session(request)(AgentEpayeRegistrationController.sessionKeyAgentRef) shouldBe "HX2000"

        verify(1, postRequestedFor(urlPathEqualTo("/agent-epaye-registration/registrations"))
          .withRequestBody(
            equalToJson("""{
                           |      "agentName" : "Angela Agent",
                           |      "contactName" : "Charlie Contact",
                           |      "telephoneNumber" : "01234567890",
                           |      "emailAddress" : "foo@bar.com",
                           |      "address" : {
                           |        "addressLine1" : "1 Streety Street",
                           |        "addressLine2" : "Towny town",
                           |        "postCode" : "AA111AA"
                           |      }
                           |}
                         """.stripMargin, true, true)))
      }

      "throw an exception upon registration if there is a problem with the backend service" in {
        val request = FakeRequest().withFormUrlEncodedBody(validFormRegistrationDetails(): _*)

        stopServer()

        intercept[BadGatewayException] {
          await(controller.details(request))
        }
      }

      "retain all the form state and pass it on to the appropriate page" when {
        "amending the agent details" in {
          val request = FakeRequest().withFormUrlEncodedBody(
            validFormRegistrationDetails() ++ Seq(("amend", "agentDetails")): _*)

          val result = await(controller.details(request))

          checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.agent.title"))
          checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "agentDetails"))
        }
        "amending the contact details" in {
          val request = FakeRequest().withFormUrlEncodedBody(
            validFormRegistrationDetails() ++ Seq(("amend", "contactDetails")): _*)

          val result = await(controller.details(request))

          checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.contact.title"))
          checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "contactDetails"))
        }
        "amending the address details" in {
          val request = FakeRequest().withFormUrlEncodedBody(
            validFormRegistrationDetails() ++ Seq(("amend", "addressDetails")): _*)

          val result = await(controller.details(request))

          checkHtmlResultWithBodyText(result, htmlEscapedMessage("details.address.title"))
          checkAllFormValuesArePresent(result, validFormRegistrationDetails("pageId" -> "addressDetails"))
        }
      }

    }

    "GET /confirmation" should {
      "show confirmation page with the newly generated agent reference" in {
        val request = FakeRequest().withSession(AgentEpayeRegistrationController.sessionKeyAgentRef -> "XY2345")
        val result = await(controller.confirmation(request))

        checkHtmlResultWithBodyText(result, htmlEscapedMessage("registrationConfirmation.label"))
        checkHtmlResultWithBodyText(result, htmlEscapedMessage("XY2345"))
      }
      "return BAD_REQUEST if new generated agent reference is not in session" in {
        status(controller.confirmation(FakeRequest())) shouldBe 400
      }
    }
  }

  private def validFormRegistrationDetails(changedKeyValues: (String, String)*) = {
    Seq(
      "pageId" -> "confirmationPage",
      "agentName" -> "Angela Agent",
      "contactName" -> "Charlie Contact",
      "telephoneNumber" -> "01234567890",
      "emailAddress" -> "foo@bar.com",
      "address.addressLine1" -> "1 Streety Street",
      "address.addressLine2" -> "Towny town",
      "address.addressLine3" -> "",
      "address.addressLine4" -> "",
      "address.postcode" -> "AA111AA")
      .map { keyValue =>
        changedKeyValues.find(_._1 == keyValue._1).getOrElse(keyValue)
      }
  }

  private def checkAllFormValuesArePresent(result: Result, formKeyValues: Seq[(String, String)]) = {
    formKeyValues.map(t => htmlEscapedMessage(t._2)).foreach(checkHtmlResultWithBodyText(result, _))
  }
}
