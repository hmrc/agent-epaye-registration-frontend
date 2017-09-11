package uk.gov.hmrc.agentepayeregistrationfrontend.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import uk.gov.hmrc.agentepayeregistrationfrontend.support.WireMockSupport

trait RegistrationStub {
  me: WireMockSupport =>

  def givenRegistrationDetails = {
    stubFor(get(urlEqualTo("/agent-epaye-registration/registrations"))
      .withHeader("Authorization",containing("Bearer"))
      .willReturn(aResponse()
        .withStatus(200)
          .withBody("[]")
      )
    )
    this
  }
}
