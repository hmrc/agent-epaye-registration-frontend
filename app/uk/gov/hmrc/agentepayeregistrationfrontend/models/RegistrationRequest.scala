/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.agentepayeregistrationfrontend.models

import play.api.libs.json.Json

case class RegistrationRequest(
  agentName: String,
  contactName: String,
  emailAddress: Option[String],
  telephoneNumber: Option[String],
  address: Address)

case class PageID(pageId: String)

object RegistrationRequest {
  implicit val registrationRequestFormat = Json.format[RegistrationRequest]
}

object PageID {
  implicit val pageIdFormat = Json.format[PageID]
}
