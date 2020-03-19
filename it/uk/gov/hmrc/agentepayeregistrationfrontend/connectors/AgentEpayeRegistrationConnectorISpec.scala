package uk.gov.hmrc.agentepayeregistrationfrontend.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.agentepayeregistrationfrontend.config.AppConfig
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{ Address, RegistrationRequest }
import uk.gov.hmrc.agentepayeregistrationfrontend.support.WireMockSupport
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.http.{ BadGatewayException, BadRequestException, HeaderCarrier }
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

class AgentEpayeRegistrationConnectorISpec extends UnitSpec with GuiceOneAppPerSuite with WireMockSupport with MockitoSugar {

  override implicit lazy val app: Application = appBuilder.build()

  protected def appBuilder: GuiceApplicationBuilder = {
    new GuiceApplicationBuilder()
      .configure("microservice.services.agent-epaye-registration.port" -> wireMockPort)
  }

  private implicit val hc = HeaderCarrier()

  private lazy val connector: AgentEpayeRegistrationConnector =
    new AgentEpayeRegistrationConnector(app.injector.instanceOf[AppConfig], app.injector.instanceOf[DefaultHttpClient],
      app.injector.instanceOf[ExecutionContext])

  private val address = Address("29 Acacia Road", "Nuttytown", Some("Bannastate"), Some("Country"), "AA11AA")
  private val regRequest = RegistrationRequest(
    "Dave Agent",
    "John Contact",
    Some("0123456789"),
    Some("email@test.com"),
    address)

  "register" should {
    "return an agent reference" in {
      val agentRef = PayeAgentReference("HX1234")

      stubFor(post(urlEqualTo("/agent-epaye-registration/registrations"))
        .willReturn(
          aResponse()
            .withStatus(Status.OK)
            .withBody(Json.obj("payeAgentReference" -> agentRef.value).toString())))

      await(connector.register(regRequest)) shouldBe agentRef
    }

    "throw an exception if no connection was possible" in {
      stopServer()

      intercept[BadGatewayException] {
        await(connector.register(regRequest))
      }
    }

    "throw an exception if the response is 400" in {
      stubFor(post(urlEqualTo("/agent-epaye-registration/registrations"))
        .willReturn(
          aResponse()
            .withStatus(Status.BAD_REQUEST)))

      intercept[BadRequestException] {
        await(connector.register(regRequest))
      }
    }

  }
}
