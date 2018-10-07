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
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.GeneralPartnership
import uk.gov.hmrc.vatsignupfrontend.forms.EmailForm
import uk.gov.hmrc.vatsignupfrontend.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.{ComponentSpecBase, CustomMatchers}

class ConfirmGeneralPartnershipControllerISpec extends ComponentSpecBase with CustomMatchers {

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(GeneralPartnership)
  }

  "GET /confirm-partnership-utr" should {
    "return an OK" in {
      stubAuth(OK, successfulAuthResponse())

      val res = get("/confirm-partnership-utr", Map(SessionKeys.partnershipSautrKey -> testSaUtr, SessionKeys.vatNumberKey -> testVatNumber))

      res should have(
        httpStatus(OK)
      )
    }
  }

  "if feature switch is disabled" should {
    "return a not found" in {
      disable(GeneralPartnership)

      val res = get("/confirm-partnership-utr")

      res should have(
        httpStatus(NOT_FOUND)
      )
    }
  }

  "POST /confirm-partnership-utr" should {

    "the partnership sautr is successfully stored" when {
        "redirect to agree to receive email page" in {
          stubAuth(OK, successfulAuthResponse())
          // TODO stubStorePartnershipSautrSuccess(testVatNumber, testSaUtr)

          val res = post("/confirm-partnership-utr",
            Map(SessionKeys.partnershipSautrKey -> testSaUtr, SessionKeys.vatNumberKey -> testVatNumber))(EmailForm.email -> testEmail)

          res should have(
            httpStatus(NOT_IMPLEMENTED)
           // TODO redirectUri(routes.AgreeCaptureEmailController.show().url)
          )
        }
      }
    }

    "store partnership sautr service returned an error" should {
      "INTERNAL_SERVER_ERROR" in {
        stubAuth(OK, successfulAuthResponse())
        // TODO stubStorePartnershipSautrFailure(testVatNumber, testSaUtr)

        val res = post("/confirm-partnership-utr",
          Map(SessionKeys.partnershipSautrKey -> testSaUtr, SessionKeys.vatNumberKey -> testVatNumber))(EmailForm.email -> testEmail)

        res should have(
          httpStatus(NOT_IMPLEMENTED)
          // TODO (INTERNAL_SERVER_ERROR)
        )
      }
    }

}