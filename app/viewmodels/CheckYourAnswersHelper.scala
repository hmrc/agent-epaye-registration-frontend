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

package viewmodels

import models.CheckMode
import models.requests.DataRequest
import pages.{YourAgentNamePage, YourBusinessAddressPage, YourContactDetailsPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

trait CheckYourAnswersHelper extends SummaryListRowHelper {

  def makeRow()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =

    request.userAnswers.get(YourAgentNamePage).map { value =>

      summaryListRow(
        messages(s"agentName.checkYourAnswersLabel"),
        value,
        Some(messages(s"agentName.checkYourAnswersLabel")),
        controllers.routes.YourAgentNameController.onPageLoad(CheckMode) -> messages("site.change")
      )
    }


  def makeContactDetailsRow()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(YourContactDetailsPage).map { value =>

      val sectionLines = Seq(Some(value.contactName), value.emailAddress, value.telephoneNumber)
        .flatten.mkString("<br>")

      summaryListRow(
        messages(s"contactDetails.checkYourAnswersLabel"),
        HtmlContent(sectionLines),
        Some(messages(s"contactDetails.checkYourAnswersLabel")),
        controllers.routes.YourContactDetailsController.onPageLoad(CheckMode) -> messages("site.change")
      )
    }


  def makeBusinessAddressRow()(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(YourBusinessAddressPage).map { value =>

      val sectionLines = Seq(Some(value.addressLine1), Some(value.addressLine2), value.addressLine3, value.addressLine4, Some(value.postCode))
        .flatten.mkString("<br>")

      summaryListRow(
        messages(s"businessAddress.checkYourAnswersLabel"),
        HtmlContent(sectionLines),
        Some(messages(s"businessAddress.checkYourAnswersLabel")),
        controllers.routes.YourBusinessAddressController.onPageLoad(CheckMode) -> messages("site.change")
      )
    }


  def makeSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
    Seq(
      makeRow(),
      makeContactDetailsRow(),
      makeBusinessAddressRow()
    ).flatten



  }

}
