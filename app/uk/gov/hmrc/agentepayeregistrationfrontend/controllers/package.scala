/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.agentepayeregistrationfrontend

import play.api.data.Forms.{text, _}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Constraints, _}

package object controllers {

  object FieldMappings {
    private val postcodeWithoutSpacesRegex = "^[A-Z]{1,2}[0-9][0-9A-Z]?[0-9][A-Z]{2}$|BFPO[0-9]{1,5}$".r
    private val telephoneNumberRegex = "^[0-9 ]*$"
    private val validStringRegex = "[a-zA-Z0-9,.()\\-\\!@\\s]+"
    private val emailRegex = """^[a-zA-Z0-9-.]+?@[a-zA-Z0-9-.]+$""".r
    private val nonEmptyPostcode: Constraint[String] = Constraint[String] { fieldValue: String =>
      nonEmptyWithMessage("error.postcode.empty")(fieldValue) match {
        case i: Invalid =>
          i
        case Valid =>
          val error = "error.postcode.invalid"
          val fieldValueWithoutSpaces = fieldValue.replace(" ", "")
          postcodeWithoutSpacesRegex.unapplySeq(fieldValueWithoutSpaces)
            .map(_ => Valid)
            .getOrElse(Invalid(ValidationError(error)))
      }
    }

    // Same as play.api.data.validation.Constraints.nonEmpty but with a custom message instead of error.required
    private def nonEmptyWithMessage(messageKey: String): Constraint[String] = Constraint[String] { (o: String) =>
      if (o == null) Invalid(ValidationError(messageKey)) else if (o.trim.isEmpty) Invalid(ValidationError(messageKey)) else Valid
    }

    private val telephoneNumber: Constraint[String] = Constraint[String] { fieldValue: String =>
      Constraints.nonEmpty(fieldValue) match {
        case i: Invalid => i
        case Valid => fieldValue match {
          case value if !value.matches(telephoneNumberRegex) =>
            Invalid(ValidationError("error.telephone.invalid"))
          case _ => Valid
        }
      }
    }

    private def emailAddress: Constraint[String] = Constraint[String]("constraint.email") { e =>
      if (e == null) Invalid(ValidationError("error.email"))
      else if (e.trim.isEmpty) Invalid(ValidationError("error.email"))
      else emailRegex.findFirstMatchIn(e.trim)
        .map(_ => Valid)
        .getOrElse(Invalid("error.email"))
    }

    private def validName(messageKey: String): Constraint[String] = Constraint[String] { fieldValue: String =>
      nonEmptyWithMessage(s"error.$messageKey.empty")(fieldValue) match {
        case i @ Invalid(_) =>
          i
        case Valid =>
          if (fieldValue.matches(validStringRegex))
            Valid
          else
            Invalid(ValidationError(s"error.$messageKey.invalid"))
      }
    }

    def textWithMaxLen(maxLength: Int, messageKey: String = "error.maxLength"): Mapping[String] = {
      text verifying maxLenWithMsg(maxLength, messageKey)
    }

    def maxLenWithMsg(maxLength: Int, messageKey: String = "error.maxLength"): Constraint[String] = Constraint[String]("constraint.maxLength", maxLength) { o =>
      require(maxLength >= 0, "string maxLength must not be negative")
      if (o == null) Invalid(ValidationError(messageKey, maxLength)) else if (o.size <= maxLength) Valid else Invalid(ValidationError(messageKey, maxLength))
    }

    def postcode: Mapping[String] = textWithMaxLen(maxLength = 8, messageKey = "error.postcode.maxLength") verifying nonEmptyPostcode
    def telephone: Mapping[Option[String]] = optional(textWithMaxLen(maxLength = 35, messageKey = "error.telephone.maxLength") verifying telephoneNumber)
    def name: Mapping[String] = textWithMaxLen(maxLength = 56, messageKey = "error.agentName.maxLength") verifying (validName("agentName"))
    def contactName: Mapping[String] = textWithMaxLen(maxLength = 56, messageKey = "error.contactName.maxLength") verifying (validName("contactName"))
    def emailAddr: Mapping[Option[String]] = optional(textWithMaxLen(maxLength = 129, messageKey = "error.emailAddress.maxLength") verifying emailAddress)
    def addressLine1: Mapping[String] = textWithMaxLen(maxLength = 35, messageKey = "error.addressLine1.maxLength") verifying (validName("addressLine1"))
    def addressLine2: Mapping[String] = textWithMaxLen(maxLength = 35, messageKey = "error.addressLine2.maxLength") verifying (validName("addressLine2"))
    def addressLine3: Mapping[Option[String]] = optional(textWithMaxLen(maxLength = 35, messageKey = "error.addressLine3.maxLength") verifying (validName("addressLine3")))
    def addressLine4: Mapping[Option[String]] = optional(textWithMaxLen(maxLength = 35, messageKey = "error.addressLine4.maxLength") verifying (validName("addressLine4")))
  }
}
