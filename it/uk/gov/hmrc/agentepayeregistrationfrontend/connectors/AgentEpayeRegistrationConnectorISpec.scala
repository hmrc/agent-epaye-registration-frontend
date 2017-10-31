package uk.gov.hmrc.agentepayeregistrationfrontend.connectors

import java.net.URL

import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status
import uk.gov.hmrc.agentepayeregistrationfrontend.models.{ Address, RegistrationRequest }
import uk.gov.hmrc.agentepayeregistrationfrontend.support.WireMockSupport
import com.github.tomakehurst.wiremock.client.WireMock._
import com.kenshoo.play.metrics.Metrics
import play.api.libs.json.Json
import uk.gov.hmrc.domain.PayeAgentReference
import uk.gov.hmrc.http.BadRequestException
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.{ BadGatewayException, HeaderCarrier, HttpGet, HttpPost }

class AgentEpayeRegistrationConnectorISpec extends UnitSpec with OneAppPerSuite with WireMockSupport with MockitoSugar {
  private implicit val hc = HeaderCarrier()

  private lazy val connector: AgentEpayeRegistrationConnector =
    new AgentEpayeRegistrationConnector(new URL(s"http://localhost:$wireMockPort"), app.injector.instanceOf[HttpGet with HttpPost], mock[Metrics])

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

      stubFor(post(urlEqualTo(s"/agent-epaye-registration/registrations"))
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
      stubFor(post(urlEqualTo(s"/agent-epaye-registration/registrations"))
        .willReturn(
          aResponse()
            .withStatus(Status.BAD_REQUEST)))

      intercept[BadRequestException] {
        await(connector.register(regRequest))
      }
    }

  }
}
