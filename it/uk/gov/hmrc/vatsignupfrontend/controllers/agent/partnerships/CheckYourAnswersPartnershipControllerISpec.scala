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

package uk.gov.hmrc.vatsignupfrontend.controllers.agent.partnerships

import play.api.http.Status._
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.{FeatureSwitching, GeneralPartnershipJourney}
import uk.gov.hmrc.vatsignupfrontend.controllers.agent.{routes => agentRoutes}
import uk.gov.hmrc.vatsignupfrontend.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.StorePartnershipInformationStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.{ComponentSpecBase, CustomMatchers}
import uk.gov.hmrc.vatsignupfrontend.models.BusinessEntity.BusinessEntitySessionFormatter
import uk.gov.hmrc.vatsignupfrontend.models.{GeneralPartnership, PartnershipEntityType, PostCode}
import uk.gov.hmrc.vatsignupfrontend.utils.SessionUtils.jsonSessionFormatter

class CheckYourAnswersPartnershipControllerISpec extends ComponentSpecBase with CustomMatchers with FeatureSwitching {

  "GET /client/check-your-answers" should {
    "return an OK" in {
      enable(GeneralPartnershipJourney)

      stubAuth(OK, successfulAuthResponse(agentEnrolment))

      val res = get("/client/check-your-answers",
        Map(
          SessionKeys.vatNumberKey -> testVatNumber,
          SessionKeys.partnershipSautrKey -> testSaUtr,
          SessionKeys.businessPostCodeKey -> jsonSessionFormatter[PostCode].toString(testBusinessPostCode),
          SessionKeys.businessEntityKey -> BusinessEntitySessionFormatter.toString(GeneralPartnership)
        )
      )

      res should have(
        httpStatus(OK)
      )
    }

    "return an NOT FOUND" in {
      disable(GeneralPartnershipJourney)
      stubAuth(OK, successfulAuthResponse(agentEnrolment))

      val res = get("/client/check-your-answers")

      res should have(
        httpStatus(NOT_FOUND)
      )
    }
  }

  "POST /client/partnership-check-your-answers" when {
    "store vat is successful" should {
      "redirect to capture email" in {
        enable(GeneralPartnershipJourney)
        stubAuth(OK, successfulAuthResponse(agentEnrolment))
        stubStorePartnershipInformation(
          testVatNumber,
          testSaUtr,
          PartnershipEntityType.GeneralPartnership,
          None,
          Some(testBusinessPostCode)
        )(
          NO_CONTENT
        )

        val res = post("/client/check-your-answers",
          Map(
            SessionKeys.vatNumberKey -> testVatNumber,
            SessionKeys.partnershipSautrKey -> testSaUtr,
            SessionKeys.businessPostCodeKey -> jsonSessionFormatter[PostCode].toString(testBusinessPostCode)
          )
        )()

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(agentRoutes.CaptureAgentEmailController.show().url)
        )
      }
    }
  }

}
