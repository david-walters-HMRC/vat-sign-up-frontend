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

package uk.gov.hmrc.vatsignupfrontend.controllers.principal

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.{InternalServerException, NotFoundException}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.GovernmentOrganisationJourney
import uk.gov.hmrc.vatsignupfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsignupfrontend.helpers.TestConstants._
import uk.gov.hmrc.vatsignupfrontend.httpparsers.StoreGovernmentOrganisationInformationHttpParser._
import uk.gov.hmrc.vatsignupfrontend.services.mocks.MockStoreGovernmentOrganisationInformationService

import scala.concurrent.Future

class GovernmentOrganisationResolverControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents
  with MockStoreGovernmentOrganisationInformationService {

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(GovernmentOrganisationJourney)
  }

  object TestGovernmentOrganisationResolverController extends GovernmentOrganisationResolverController(
    mockControllerComponents,
    mockStoreGovernmentOrganisationInformationService
  )

  lazy val testGetRequest = FakeRequest("GET", "/government-organisation-resolver")

  "calling the resolve method on GovernmentOrganisationResolverController" when {
    "the Government Organisation feature switch is on" when {
      "StoreGovernmentOrganisationInformation returns StoreGovernmentOrganisationInformationSuccess" should {
        "goto agree capture email" in {
          mockAuthAdminRole()
          mockStoreGovernmentOrganisationInformation(testVatNumber)(
            Future.successful(Right(StoreGovernmentOrganisationInformationSuccess))
          )

          val res = await(TestGovernmentOrganisationResolverController.resolve(testGetRequest.withSession(
            SessionKeys.vatNumberKey -> testVatNumber
          )))

          status(res) shouldBe SEE_OTHER
          redirectLocation(res) shouldBe Some(routes.DirectDebitResolverController.show().url)
        }
      }
      "StoreGovernmentOrganisationInformation returns StoreGovernmentOrganisationInformationFailureResponse" should {
        "throw internal server exception" in {
          mockAuthAdminRole()
          mockStoreGovernmentOrganisationInformation(testVatNumber)(
            Future.successful(Left(StoreGovernmentOrganisationInformationFailureResponse(INTERNAL_SERVER_ERROR)))
          )

          intercept[InternalServerException] {
            await(TestGovernmentOrganisationResolverController.resolve(testGetRequest.withSession(
              SessionKeys.vatNumberKey -> testVatNumber
            )))
          }
        }
      }
      "vat number is not in session" should {
        "goto resolve vat number" in {
          mockAuthAdminRole()
          mockStoreGovernmentOrganisationInformation(testVatNumber)(
            Future.successful(Right(StoreGovernmentOrganisationInformationSuccess))
          )

          val res = await(TestGovernmentOrganisationResolverController.resolve(testGetRequest))

          status(res) shouldBe SEE_OTHER
          redirectLocation(res) shouldBe Some(routes.ResolveVatNumberController.resolve().url)
        }
      }
    }

    "the Government Organisation feature switch is off" should {
      "throw not found exception" in {
        disable(GovernmentOrganisationJourney)

        intercept[NotFoundException] {
          await(TestGovernmentOrganisationResolverController.resolve(testGetRequest.withSession(
            SessionKeys.vatNumberKey -> testVatNumber
          )))
        }
      }
    }
  }

}

