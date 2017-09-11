package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import play.api.{Application, Mode}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.testonly.TestOnlyController
import uk.gov.hmrc.agentepayeregistrationfrontend.stubs.{AuthStub, RegistrationStub}
import uk.gov.hmrc.play.http.SessionKeys

class TestOnlyControllerISpec extends BaseControllerISpec with AuthStub with RegistrationStub {

  override implicit lazy val app: Application = appBuilder
    .build()

  override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "microservice.services.agent-epaye-registration.port" -> wireMockPort,
        "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
        "extract.auth.stride.enrolment" -> "ValidStrideEnrolment"
      ).in(Mode.Test)

  private lazy val controller = app.injector.instanceOf[TestOnlyController]

  "TestOnlyController" should {
    "return 200 OK after successful authorisation" in {
      givenAuthorisedFor("ValidStrideEnrolment", "PrivilegedApplication")
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
        "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
        "extract.auth.stride.enrolment" -> "ValidStrideEnrolment"
      ).in(Mode.Dev)

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

