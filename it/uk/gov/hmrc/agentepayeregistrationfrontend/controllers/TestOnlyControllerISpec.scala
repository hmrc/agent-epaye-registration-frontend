package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.{ Application, Mode }
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.testonly.TestOnlyController
import uk.gov.hmrc.agentepayeregistrationfrontend.stubs.{ AuthStub, RegistrationStub }
import uk.gov.hmrc.http.{ HeaderCarrier, SessionKeys }

import scala.concurrent.ExecutionContext.Implicits.global

class TestOnlyControllerISpec extends BaseControllerISpec with AuthStub with RegistrationStub {

  override implicit lazy val app: Application = appBuilder
    .build()

  override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "microservice.services.agent-epaye-registration.port" -> wireMockPort,
        "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes").in(Mode.Test)

  private lazy val controller = app.injector.instanceOf[TestOnlyController]

  "TestOnlyController" should {
    "return 200 OK after successful authorisation" in {
      givenAuthorisedFor("T2 Technical", "PrivilegedApplication")
      givenRegistrationDetails
      val result = await(controller.extract(authenticatedRequest("GET", "/agent-epaye-registration/test-only/extract")))

      status(result) shouldBe 200
    }

    "return 403 FORBIDDEN for an invalid stride enrolment" in {
      givenAuthorisedFor("InValidStrideEnrolment", "PrivilegedApplication")
      givenRegistrationDetails
      val result = await(controller.extract(authenticatedRequest("GET", "/agent-epaye-registration/test-only/extract")))

      status(result) shouldBe 403
    }

    "redirect to stride login if the request is not authorised " in {
      givenRequestIsNotAuthorised("MissingBearerToken")
      givenRegistrationDetails
      val result = await(controller.extract(FakeRequest()))

      status(result) shouldBe 303
    }

    "proxy pass request to upstream service and return response" when {
      "empty content and no headers" in {
        stubFor(get(urlPathMatching("/test"))
          .willReturn(aResponse()
            .withStatus(202)))
        val result = await(controller.proxyPassTo(s"$wireMockBaseUrlAsString/test")(FakeRequest("GET", ""), HeaderCarrier()))
        status(result) shouldBe 202
        result.body.contentType shouldBe None
        result.body.contentLength shouldBe None
        await(result.body.consumeData).isEmpty shouldBe true

      }
      "non-empty content and some headers" in {
        stubFor(get(urlPathMatching("/test"))
          .willReturn(aResponse()
            .withStatus(203)
            .withBody("foobar" * 1000)
            .withHeader("Foo", "Bar")
            .withHeader("Content-Type", "foo/bar")
            .withHeader("Content-Length", "6000")))
        val result = await(controller.proxyPassTo(s"$wireMockBaseUrlAsString/test")(FakeRequest("GET", ""), HeaderCarrier()))
        status(result) shouldBe 203
        result.body.contentType shouldBe Some("foo/bar")
        result.body.contentLength shouldBe Some(6000)
        await(result.body.consumeData.map(_.utf8String)) shouldBe ("foobar" * 1000)
      }
    }
  }

  private def authenticatedRequest(method: String, path: String): FakeRequest[AnyContentAsEmpty.type] = {
    FakeRequest(method, path).withSession(SessionKeys.authToken -> "Bearer XYZ")
  }
}

class TestOnlyControllerDevModeISpec extends BaseControllerISpec with AuthStub with RegistrationStub {

  override implicit lazy val app: Application = appBuilder
    .build()

  override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "microservice.services.agent-epaye-registration.port" -> wireMockPort,
        "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes").in(Mode.Dev)

  private lazy val controller = app.injector.instanceOf[TestOnlyController]

  "TestOnlyController in Dev mode" should {
    "redirect to stride login if the request is not authorised " in {
      givenRequestIsNotAuthorised("MissingBearerToken")
      givenRegistrationDetails
      val result = await(controller.extract(FakeRequest()))

      status(result) shouldBe 303
    }
  }
}

