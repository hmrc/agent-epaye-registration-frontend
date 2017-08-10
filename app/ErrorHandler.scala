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

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.http.HeaderNames.CACHE_CONTROL
import play.api.http.HttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(implicit configuration: Configuration, val messagesApi: MessagesApi) extends HttpErrorHandler with I18nSupport {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future successful
      Status(statusCode)(uk.gov.hmrc.agentepayeregistrationfrontend.views.html.error_template(Messages(s"global.error.$statusCode.title"),
        Messages(s"global.error.$statusCode.heading"), Messages(s"global.error.$statusCode.message")))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future successful InternalServerError(uk.gov.hmrc.agentepayeregistrationfrontend.views.html.error_template(Messages("global.error.badRequest500.title"),
      Messages("global.error.500.heading"), Messages("global.error.500.heading"))).withHeaders(CACHE_CONTROL -> "no-cache")
  }
}