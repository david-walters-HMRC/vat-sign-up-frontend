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

package uk.gov.hmrc.vatsignupfrontend.httpparsers

import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsignupfrontend.httpparsers.StoreGovernmentOrganisationInformationHttpParser._

class StoreGovernmentOrganisationInformationHttpParserSpec extends UnitSpec {
  val testHttpVerb = "POST"
  val testUri = "/"

  "StoreGovernmentOrganisationInformationHttpReads" when {
    "read" should {
      "parse a NO_CONTENT response as an StoreGovernmentOrganisationInformationSuccess" in {
        val httpResponse = HttpResponse(NO_CONTENT)

        val res = StoreGovernmentOrganisationInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldBe Right(StoreGovernmentOrganisationInformationSuccess)
      }

      "parse any other response as a StoreGovernmentOrganisationInformationFailureResponse" in {
        val httpResponse = HttpResponse(BAD_REQUEST)

        val res = StoreGovernmentOrganisationInformationHttpReads.read(testHttpVerb, testUri, httpResponse)

        res shouldEqual Left(StoreGovernmentOrganisationInformationFailureResponse(BAD_REQUEST))
      }

    }
  }

}
