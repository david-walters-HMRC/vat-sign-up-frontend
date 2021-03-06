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

package uk.gov.hmrc.vatsignupfrontend.controllers.agent

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.vatsignupfrontend.config.mocks.MockControllerComponents
import uk.gov.hmrc.vatsignupfrontend.views.html.agent.failed_client_matching
import play.api.i18n.Messages.Implicits._

class FailedClientMatchingControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockControllerComponents {

  object TestFailedClientMatchingController extends FailedClientMatchingController(mockControllerComponents)

  implicit lazy val testGetRequest = FakeRequest("GET", "/error/incorrect-details")

  "Calling the show action of the Failed Client Matching controller" should {
    "return OK" in {
      mockAuthRetrieveAgentEnrolment()
      val request = testGetRequest

      val result = TestFailedClientMatchingController.show(request)
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "go to the Sign up Another Client page" in {
    mockAuthRetrieveAgentEnrolment()
    val result = await(TestFailedClientMatchingController.show(testGetRequest))

    status(result) shouldBe Status.OK
    contentAsString(result) shouldBe failed_client_matching(routes.SignUpAnotherClientController.submit()).body
  }
}
