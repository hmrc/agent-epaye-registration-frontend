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

import forms.mappings.Mappings
import models.YourBusinessAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class YourBusinessAddressFormProvider @Inject() extends Mappings {

  val maxAddressLineLength = 35
  val maxPostcodeLength = 10

  def apply(): Form[YourBusinessAddress] = Form(
    mapping(
      "addressLine1" -> text(
        errorKey = "yourBusinessAddress.addressLine1.error.required"
      ).verifying(
        maxLength(
          maxAddressLineLength, "yourBusinessAddress.addressLine1.error.length"
        ),
        validCharacters("yourBusinessAddress.addressLine1.error.invalidCharacters")
      ),
      "addressLine2" -> text(
        errorKey = "yourBusinessAddress.addressLine2.error.required"
      ).verifying(
        maxLength(
          maxAddressLineLength, "yourBusinessAddress.addressLine2.error.length"
        ),
        validCharacters("yourBusinessAddress.addressLine2.error.invalidCharacters")
      ),
      "addressLine3" -> optional(
        text(
          errorKey = "yourBusinessAddress.addressLine3.error.required"
        ).verifying(
          maxLength(
            maxAddressLineLength, "yourBusinessAddress.addressLine3.error.length"
          ),
          validCharacters("yourBusinessAddress.addressLine3.error.invalidCharacters")
        )
      ),
      "addressLine4" -> optional(
        text(
          errorKey = "yourBusinessAddress.addressLine4.error.required"
        ).verifying(
          maxLength(
            maxAddressLineLength, "yourBusinessAddress.addressLine4.error.length"
          ),
          validCharacters("yourBusinessAddress.addressLine4.error.invalidCharacters")
        )
      ),
      "postCode" ->
        addressPostcode(
          "yourBusinessAddress.postCode.error.required",
          "yourBusinessAddress.postCode.error.invalid"
        ).verifying(
          maxLength(
            maxPostcodeLength,
            "yourBusinessAddress.postCode.error.length"
          )
        )
    )(YourBusinessAddress.apply)(YourBusinessAddress.unapply)
  )
}
