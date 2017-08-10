package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.agentepayeregistrationfrontend.service.AgentEpayeRegistrationService

class AgentEpayeRegistrationControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with BeforeAndAfterEach {

  val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  val mockConfig: Configuration = app.injector.instanceOf[Configuration]
  val mockService: AgentEpayeRegistrationService = mock[AgentEpayeRegistrationService]

  val controller = new AgentEpayeRegistrationController(messagesApi, mockService)(mockConfig)

  "showRegistrationForm shows the registration form" in {
    val response = controller.register()(FakeRequest("GET", "/"))

    status(response) mustBe OK
    contentType(response) mustBe HTML
    contentAsString(response) must include(messagesApi(""))
  }

  "register" should {
    "check fields are valid" in {
      val response = controller.register()(FakeRequest("GET", "/hello-world"))

      status(response) mustBe OK
      contentType(response).get mustBe HTML
      contentAsString(response) must include(messagesApi("app.name"))
    }
  }
}