/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.http.BadGatewayException

import scala.concurrent.Future

class ErrorHandlerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val messagesApi = app.injector.instanceOf[MessagesApi]
  val handler = new ErrorHandler()(app.injector.instanceOf[Configuration], messagesApi)

  "ErrorHandler should show the error page" when {
    "a server error occurs" in {
      val result = handler.onServerError(FakeRequest(), new BadGatewayException(""))

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some(HTML)
      checkIncludesMessages(result, "global.error.500.title", "global.error.500.heading", "global.error.500.message")
    }

    "a client error (400) occurs" in {
      val result = handler.onClientError(FakeRequest(), BAD_REQUEST, "")

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some(HTML)
      checkIncludesMessages(result, "global.error.400.title", "global.error.400.heading", "global.error.400.message")
    }

    "a client error (404) occurs" in {
      val result = handler.onClientError(FakeRequest(), NOT_FOUND, "")

      status(result) mustBe NOT_FOUND
      contentType(result) mustBe Some(HTML)
      checkIncludesMessages(result, "global.error.404.title", "global.error.404.heading", "global.error.404.message")
    }
  }

  private def checkIncludesMessages(result: Future[Result], messageKeys: String*): Unit = {
    messageKeys.foreach{ messageKey =>
      messagesApi.isDefinedAt(messageKey) mustBe true
      contentAsString(result) must include(HtmlFormat.escape(messagesApi(messageKey)).toString)
    }
  }
}
