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

package uk.gov.hmrc.agentepayeregistrationfrontend.models

import uk.gov.hmrc.play.test.UnitSpec

class AddressSpec extends UnitSpec {
  val testAddress: Address = new Address(
    "addressLine1",
    "addressLine2",
    Some("addressLine3"),
    Some("addressLine4"),
    "AA1 1AA")

  val testAddress2: Address = new Address(
    "addressLine1",
    "addressLine2",
    Some("addressLine3"),
    Some("addressLine4"),
    "AB123CD")

  "withoutSpacesInPostCode" should {
    "remove all spaces in a postCode ith spaces" in {
      testAddress.withoutSpacesInPostCode shouldBe "AA11AA"
    }

    "leave a postCode with no spaces unchanged" in {
      testAddress2.withoutSpacesInPostCode shouldBe "AB123CD"
    }
  }
}
