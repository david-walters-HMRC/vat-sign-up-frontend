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
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.LimitedPartnershipJourney
import uk.gov.hmrc.vatsignupfrontend.controllers.principal.{routes => principalRoutes}
import uk.gov.hmrc.vatsignupfrontend.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsignupfrontend.models.PartnershipEntityType.LimitedPartnership

class ConfirmPartnershipControllerISpec extends ComponentSpecBase with CustomMatchers {

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(LimitedPartnershipJourney)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    disable(LimitedPartnershipJourney)
  }

  "GET /confirm-partnership-company" when {
    "LimitedPartnershipJourney is disabled" should {
      "return an NOT_FOUND" in {
        disable(LimitedPartnershipJourney)

        val res = get("/confirm-partnership-company")

        res should have(
          httpStatus(NOT_FOUND)
        )
      }
    }

    "LimitedPartnershipJourney is enabled" should {
      "return an OK" in {

        stubAuth(OK, successfulAuthResponse())

        val res = get("/confirm-partnership-company", Map(
          SessionKeys.vatNumberKey -> testVatNumber,
          SessionKeys.companyNumberKey -> testCompanyNumber,
          SessionKeys.companyNameKey -> testCompanyName,
          SessionKeys.partnershipTypeKey -> LimitedPartnership.toString)
        )

        res should have(
          httpStatus(OK)
        )
      }

      "redirect to resolve vat number" when {
        "there is no vat number in session" in{
          stubAuth(OK, successfulAuthResponse())

          val res = get("/confirm-partnership-company", Map(
            SessionKeys.companyNameKey -> testCompanyName,
            SessionKeys.companyNumberKey -> testCompanyNumber,
            SessionKeys.partnershipTypeKey -> LimitedPartnership.toString)
          )

          res should have(
            httpStatus(SEE_OTHER),
              redirectUri(principalRoutes.ResolveVatNumberController.resolve().url)
          )
        }
      }

      "redirect to capture partnership company number page" when {
        "there is no company name in session" in{
          stubAuth(OK, successfulAuthResponse())

          val res = get("/confirm-partnership-company", Map(
            SessionKeys.companyNumberKey -> testCompanyNumber,
            SessionKeys.companyNameKey -> testCompanyName,
            SessionKeys.partnershipTypeKey -> LimitedPartnership.toString)
          )

          res should have(
            httpStatus(SEE_OTHER),
            redirectUri(principalRoutes.ResolveVatNumberController.resolve().url)
          )
        }
      }

    }
  }

  "POST /confirm-partnership-company" should {

      "LimitedPartnershipJourney is disabled" should {
        "return an NOT_FOUND" in {
          disable(LimitedPartnershipJourney)

          val res = post("/confirm-partnership-company")()

          res should have(
            httpStatus(NOT_FOUND)
          )
        }
      }

    "redirect to resolve partnership utr" in {

      stubAuth(OK, successfulAuthResponse())

      val res = post("/confirm-partnership-company",
        Map(
          SessionKeys.vatNumberKey -> testVatNumber,
          SessionKeys.companyNumberKey -> testCompanyNumber,
          SessionKeys.companyNameKey -> testCompanyName,
          SessionKeys.partnershipTypeKey -> LimitedPartnership.toString
        ))()

      res should have(
        httpStatus(SEE_OTHER),
        redirectUri(routes.ResolvePartnershipUtrController.resolve().url)
      )

    }
  }

  "if there is not company name in session" should {
    "redirect to capture partnership company number page" in {

      stubAuth(OK, successfulAuthResponse())

      val res = post("/confirm-partnership-company",
        Map(
          SessionKeys.vatNumberKey -> testVatNumber,
          SessionKeys.companyNumberKey -> testCompanyNumber,
          SessionKeys.partnershipTypeKey -> LimitedPartnership.toString
        ))()

      res should have(
        httpStatus(SEE_OTHER),
        redirectUri(routes.CapturePartnershipCompanyNumberController.show().url)
      )
    }
  }

}