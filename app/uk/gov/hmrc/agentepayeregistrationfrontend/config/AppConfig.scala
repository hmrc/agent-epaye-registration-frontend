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

package uk.gov.hmrc.agentepayeregistrationfrontend.config

import javax.inject.Inject
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import play.api.Configuration

class AppConfig @Inject() (
  val runModeConfiguration: Configuration,
  config: ServicesConfig) {

  private def loadConfig(key: String) = runModeConfiguration.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  lazy val opraUrl: String = config.baseUrl("agent-epaye-registration")
  lazy val auth: String = config.baseUrl("auth")
  lazy val enrolment: String = config.getString("extract.auth.stride.enrolment")
  lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  lazy val googleTagManagerId = loadConfig(s"google-tag-manager.id")

}
