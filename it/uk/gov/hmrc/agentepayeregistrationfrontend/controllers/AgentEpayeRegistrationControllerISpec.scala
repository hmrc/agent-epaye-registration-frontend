package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import org.scalatestplus.play.OneAppPerSuite
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType}
import uk.gov.hmrc.agentepayeregistrationfrontend.support.WireMockSupport
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

class AgentEpayeRegistrationControllerISpec extends UnitSpec with OneAppPerSuite with WireMockSupport {
  override implicit lazy val app: Application = appBuilder.build()

  private val messagesApi = app.injector.instanceOf[MessagesApi]

  protected implicit val materializer = app.materializer

  implicit def hc(implicit request: FakeRequest[_]): HeaderCarrier = HeaderCarrier.fromHeadersAndSession(request.headers, Some(request.session))

  protected def appBuilder: GuiceApplicationBuilder = {
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "microservice.services.agent-subscription.port" -> wireMockPort,
        "microservice.services.address-lookup-frontend.port" -> wireMockPort
      )
  }

  private lazy val controller: AgentEpayeRegistrationController = app.injector.instanceOf[AgentEpayeRegistrationController]

  "showRegistrationForm shows the registration form page" in {
    val result = await(controller.showRegistrationForm(FakeRequest()))

    checkHtmlResultWithBodyText(result, messagesApi("registrationConfirmation.title"))
  }

  protected def checkHtmlResultWithBodyText(result: Result, expectedSubstrings: String*): Unit = {
    status(result) shouldBe 200
    contentType(result) shouldBe Some("text/html")
    charset(result) shouldBe Some("utf-8")
    expectedSubstrings.foreach(s => bodyOf(result) should include(s))
  }
}
