import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.agentepayeregistrationfrontend.controllers.BaseControllerISpec
import uk.gov.hmrc.play.http.BadGatewayException

class ErrorHandlerISpec extends BaseControllerISpec {
  implicit val config = app.configuration
  implicit val messagesApi = app.injector.instanceOf[MessagesApi]

  "a server error will result in the error page being shown" in {
    val handler = new ErrorHandler()

    val result = await(handler.onServerError(FakeRequest(), new BadGatewayException("")))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("global.error.500.title"), 500)
    checkHtmlResultWithBodyText(result, htmlEscapedMessage("global.error.500.heading"), 500)
  }

  "a client error will result in the error page being shown" in {
    val handler = new ErrorHandler()

    val result = await(handler.onClientError(FakeRequest(), 400, ""))

    checkHtmlResultWithBodyText(result, htmlEscapedMessage("global.error.400.title"), 400)
    checkHtmlResultWithBodyText(result, htmlEscapedMessage("global.error.400.heading"), 400)
  }
}
