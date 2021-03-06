/*
 * Copyright 2019 HM Revenue & Customs
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

//$COVERAGE-OFF$Disabling scoverage

package uk.gov.hmrc.vatsignupfrontend.testonly.connectors

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsignupfrontend.config.AppConfig
import play.api.http.Status.OK
import uk.gov.hmrc.vatsignupfrontend.models.UserDetailsModel
import uk.gov.hmrc.vatsignupfrontend.testonly.httpparsers.MatchingStubHttpParser.MatchingStubResponse
import uk.gov.hmrc.vatsignupfrontend.testonly.httpparsers.MatchingStubHttpParser._
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import scala.concurrent.Future


case class Request(data: UserDetailsModel,
                   testId: String = "VATSUBSC",
                   name: String = "CID",
                   service: String = "find",
                   resultCode: Option[Int] = Some(OK),
                   delay: Option[Int] = None,
                   timeToLive: Option[Int] = Some(120000 * 60)
                  )

object Request {

  import UserDetailsModel.matchingStubWrites

  private implicit val customUserDetailsWrites = matchingStubWrites
  implicit val format = Json.format[Request]
}

@Singleton
class MatchingStubConnector @Inject()(val http: HttpClient,
                                      val applicationConfig: AppConfig) {

  def stubUser(userData: UserDetailsModel)(implicit hc: HeaderCarrier): Future[MatchingStubResponse] = {
    val requestModel = Request(userData)
    http.POST[Request, MatchingStubResponse](applicationConfig.stubCitizenDetailsUserUrl, requestModel)
  }
}
// $COVERAGE-ON$