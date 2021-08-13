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

import forms.behaviours.{EmailBehaviours, PostCodeBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class YourBusinessAddressFormProviderSpec extends StringFieldBehaviours with EmailBehaviours with PostCodeBehaviours {

  val form = new YourBusinessAddressFormProvider()()

  val addressLineMaxLength = 35

  ".addressLine1" - {
    val requiredKey = "yourBusinessAddress.addressLine1.error.required"
    val lengthKey = "yourBusinessAddress.addressLine1.error.length"

    val fieldName = "addressLine1"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".addressLine2" - {
    val requiredKey = "yourBusinessAddress.addressLine2.error.required"
    val lengthKey = "yourBusinessAddress.addressLine2.error.length"

    val fieldName = "addressLine2"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".addressLine3" - {
    val lengthKey = "yourBusinessAddress.addressLine3.error.length"

    val fieldName = "addressLine3"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )
  }

  ".addressLine4" - {
    val lengthKey = "yourBusinessAddress.addressLine4.error.length"

    val fieldName = "addressLine4"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(addressLineMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(addressLineMaxLength))
    )
  }

  ".postCode" - {
    val keyLength = "yourBusinessAddress.postCode.error.length"
    val keyInvalid = "yourBusinessAddress.postCode.error.invalid"

    val fieldName = "postCode"

    behave like formWithPostCodeField(
      form,
      fieldName,
      keyLength,
      keyInvalid
    )
  }
}
