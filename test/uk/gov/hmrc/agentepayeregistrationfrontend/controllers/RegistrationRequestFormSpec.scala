/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import uk.gov.hmrc.agentepayeregistrationfrontend.models.{ Address, RegistrationRequest }
import org.scalatestplus.play.PlaySpec

class RegistrationRequestFormSpec extends PlaySpec {

  "agentDetailsForm" should {

    "bind input fields and return RegistrationRequest and fill it back" in {
      val form = AgentEpayeRegistrationController.agentDetailsForm

      val value = RegistrationRequest(
        agentName = "agentName",
        contactName = "",
        emailAddress = None,
        telephoneNumber = None,
        address = Address(
          addressLine1 = "",
          addressLine2 = "",
          addressLine3 = None,
          addressLine4 = None,
          postCode = ""))

      val fieldValues = Map(
        "agentName" -> "agentName",
        "contactName" -> "",
        "address.addressLine1" -> "",
        "address.addressLine2" -> "",
        "address.postcode" -> "")

      form.bind(fieldValues).value mustBe Some(value)
      form.fill(value).data mustBe fieldValues
    }
  }

  "contactDetailsForm" should {

    "bind input fields and return RegistrationRequest and fill it back" in {
      val form = AgentEpayeRegistrationController.contactDetailsForm

      val value = RegistrationRequest(
        agentName = "agentName",
        contactName = "contactName",
        emailAddress = Some("foo@bar.com"),
        telephoneNumber = Some("098765321"),
        address = Address(
          addressLine1 = "",
          addressLine2 = "",
          addressLine3 = None,
          addressLine4 = None,
          postCode = ""))

      val fieldValues = Map(
        "agentName" -> "agentName",
        "contactName" -> "contactName",
        "emailAddress" -> "foo@bar.com",
        "telephoneNumber" -> "098765321",
        "address.addressLine1" -> "",
        "address.addressLine2" -> "",
        "address.postcode" -> "")

      form.bind(fieldValues).value mustBe Some(value)
      form.fill(value).data mustBe fieldValues
    }
  }

  "RegistrationRequestForm" should {

    "bind input fields and return RegistrationRequest and fill it back" in {
      val form = AgentEpayeRegistrationController.registrationRequestForm

      val value = RegistrationRequest(
        agentName = "agentName",
        contactName = "contactName",
        emailAddress = Some("foo@bar.com"),
        telephoneNumber = Some("098765321"),
        address = Address(
          addressLine1 = "Address line 1",
          addressLine2 = "Sometown Somewhere",
          addressLine3 = Some("Address line 3"),
          addressLine4 = Some("Address line 4"),
          postCode = "AA1 1AA"))

      val fieldValues = Map(
        "agentName" -> "agentName",
        "contactName" -> "contactName",
        "emailAddress" -> "foo@bar.com",
        "telephoneNumber" -> "098765321",
        "address.addressLine1" -> "Address line 1",
        "address.addressLine2" -> "Sometown Somewhere",
        "address.addressLine3" -> "Address line 3",
        "address.addressLine4" -> "Address line 4",
        "address.postcode" -> "AA1 1AA")

      form.bind(fieldValues).value mustBe Some(value)
      form.fill(value).data mustBe fieldValues
    }
  }
}
