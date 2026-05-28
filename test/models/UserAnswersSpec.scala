/*
 * Copyright 2023 HM Revenue & Customs
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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsNumber, JsString, JsSuccess, JsValue, Json}

import java.time.{LocalDateTime, ZoneOffset}

class UserAnswersSpec extends AnyWordSpec with Matchers {

  val jsonString: String =
    """{
      |  "_id": "some_id_123",
      |  "data": {
      |    "key1": "value1",
      |    "key2": 2,
      |    "key3": "value3",
      |    "key4": [
      |      "value4item1",
      |      "value4item2"
      |    ]
      |  },
      |  "lastUpdated": {
      |    "$date":{
      |      "$numberLong":"1778853155123"
      |    }
      |  }
      |}
      |""".stripMargin

  val jsValue: JsValue = Json.parse(jsonString)

  val userAnswers = UserAnswers(
    _id = "some_id_123",
    data = Json.obj(
      "key1" -> JsString("value1"),
      "key2" -> JsNumber(2),
      "key3" -> JsString("value3"),
      "key4" -> Json.arr(
        JsString("value4item1"),
        JsString("value4item2")
      )
    ),
    lastUpdated = LocalDateTime
      .of(2026, 5, 15, 13, 52, 35, 123000000)
      .toInstant(ZoneOffset.UTC)
  )

  "UserAnswers.reads" should {
    "read correctly when all fields are provided" in {
      jsValue.validate[UserAnswers] shouldBe JsSuccess(userAnswers)
    }
  }

  "UserAnswers.writes" should {
    "write correct value to JSON" in {
      Json.toJson(userAnswers) shouldBe jsValue
    }
  }

}
