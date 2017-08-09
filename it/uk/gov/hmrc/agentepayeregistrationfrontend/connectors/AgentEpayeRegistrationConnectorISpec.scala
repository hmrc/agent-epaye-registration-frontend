package uk.gov.hmrc.agentepayeregistrationfrontend.connectors

import java.net.URL

import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.agentepayeregistrationfrontend.support.WireMockSupport
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import wiring.WSVerbs

class AgentEpayeRegistrationConnectorISpec extends UnitSpec with OneAppPerSuite with WireMockSupport {
  private implicit val hc = HeaderCarrier()

  private lazy val connector: AgentEpayeRegistrationConnector =
    new AgentEpayeRegistrationConnector(new URL(s"http://localhost:$wireMockPort"), new WSVerbs)

  "re"


}
