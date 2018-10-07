/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.vatsignupfrontend.controllers.principal.partnerships

import play.api.http.Status._
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.GeneralPartnership
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.{ComponentSpecBase, CustomMatchers}

class ResolvePartnershipUtrControllerISpec extends ComponentSpecBase with CustomMatchers {


  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(GeneralPartnership)
  }

  "GET /resolve-partnership-utr" when {
    "the partnership utr is on the profile" should {
      "redirect to the confirm general partnership page" in {
        stubAuth(OK, successfulAuthResponse(partnershipEnrolment))

        val res = get("/resolve-partnership-utr")

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.ConfirmGeneralPartnershipController.show().url)
        )
      }
    }

    "the partnership utr is not on the profile" when {
      "throw Internal Server Error" in {
        stubAuth(OK, successfulAuthResponse())

        val res = get("/resolve-partnership-utr")

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)

        )
      }
    }
  }

}
