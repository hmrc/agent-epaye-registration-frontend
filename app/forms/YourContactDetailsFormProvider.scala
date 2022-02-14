/*
 * Copyright 2022 HM Revenue & Customs
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
import models.YourContactDetails
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class YourContactDetailsFormProvider @Inject() extends Mappings {

  val contactNameMaxLength = 56

  def apply(): Form[YourContactDetails] = Form(
    mapping(
      "contactName" -> text("yourContactDetails.contactName.error.required").verifying(
        maxLength(
          contactNameMaxLength, "yourContactDetails.contactName.error.length"
        ),
        validCharacters("yourContactDetails.contactName.error.invalidCharacters")
      ),
      "emailAddress" ->
        optional(
          email(
            "yourContactDetails.emailAddress.error.required",
            "yourContactDetails.emailAddress.error.invalid",
            "yourContactDetails.emailAddress.error.length"
          )
        ),
      "telephoneNumber" ->
        optional(
          telephone(
            "yourContactDetails.telephoneNumber.error.required",
            "yourContactDetails.telephoneNumber.error.invalid",
            "yourContactDetails.telephoneNumber.error.length"
          )
        )
    )(YourContactDetails.apply)(YourContactDetails.unapply)
  )

}