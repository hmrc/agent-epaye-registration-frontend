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

package generators

import models.{YourBusinessAddress, YourContactDetails}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

trait ModelGenerators {

  implicit lazy val arbitraryYourContactDetails: Arbitrary[YourContactDetails] =
    Arbitrary {
      for {
        contactName <- arbitrary[String]
        emailAddress <- arbitrary[String]
        telephoneNumber <- arbitrary[String]
      } yield YourContactDetails(contactName, Some(emailAddress), Some(telephoneNumber))
    }


  implicit lazy val arbitraryBusinessAddress: Arbitrary[YourBusinessAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[String]
        line4 <- arbitrary[String]
        postCode <- arbitrary[String]
      } yield YourBusinessAddress(line1, line2, Some(line3), Some(line4), postCode)
    }

}
