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

package forms

import forms.behaviours.{EmailBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class YourContactDetailsFormProviderSpec extends StringFieldBehaviours with EmailBehaviours {

  val form = new YourContactDetailsFormProvider()()

  ".contactName" - {
    val requiredKey = "yourContactDetails.contactName.error.required"
    val lengthKey = "yourContactDetails.contactName.error.length"
    val maxLength = 56

    val fieldName = "contactName"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".telephoneNumber" - {
    val keyEmailInvalid = "yourContactDetails.telephoneNumber.error.invalid"
    val fieldName = "telephoneNumber"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0121 1234 5678"
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "ONETWOTHREEFOUR",
      FormError(fieldName, keyEmailInvalid, Seq.empty)
    )
  }

  ".emailAddress" - {
    val fieldName = "emailAddress"
    val keyEmailLength = "yourContactDetails.emailAddress.error.length"
    val keyEmailInvalid = "yourContactDetails.emailAddress.error.invalid"

    behave like formWithEmailField(
      form,
      fieldName,
      keyEmailLength,
      keyEmailInvalid
    )
  }
}
