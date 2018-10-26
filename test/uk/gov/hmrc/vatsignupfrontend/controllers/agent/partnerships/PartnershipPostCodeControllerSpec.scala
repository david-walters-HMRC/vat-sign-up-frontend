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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.GeneralPartnershipJourney
import uk.gov.hmrc.vatsignupfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsignupfrontend.forms.PartnershipPostCodeForm._
import uk.gov.hmrc.vatsignupfrontend.helpers.TestConstants._

class PartnershipPostCodeControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents {

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(GeneralPartnershipJourney)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    disable(GeneralPartnershipJourney)
  }

  object TestPartnershipPostCodeController extends PartnershipPostCodeController(mockControllerComponents)

  lazy val testGetRequest = FakeRequest("GET", "/principal-place-postcode")

  def testPostRequest(partnershipPostCodeVal: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest("POST", "/principal-place-postcode").withFormUrlEncodedBody(partnershipPostCode -> partnershipPostCodeVal)

  "Calling the show action of the Partnership PostCode controller" when {
    "go to the Partnership PostCode page" in {
      mockAuthRetrieveAgentEnrolment()

      val result = TestPartnershipPostCodeController.show(testGetRequest)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "Calling the submit action of the Partnership PostCode controller" when {
    "form successfully submitted" should {
      "Not implemented" in {
        mockAuthRetrieveAgentEnrolment()

        implicit val request = testPostRequest(testBusinessPostcode.postCode)
        val result = TestPartnershipPostCodeController.submit(request)
        status(result) shouldBe Status.NOT_IMPLEMENTED
        //TODO redirect to partnership cya page
      }
    }

    "form unsuccessfully submitted" should {
      "reload the page with errors" in {
        mockAuthRetrieveAgentEnrolment()

        val result = TestPartnershipPostCodeController.submit(testPostRequest(""))
        status(result) shouldBe Status.BAD_REQUEST
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }
  }

}
