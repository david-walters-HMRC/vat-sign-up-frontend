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

package uk.gov.hmrc.vatsubscriptionfrontend.helpers.servicemocks

import uk.gov.hmrc.vatsubscriptionfrontend.helpers.IntegrationTestConstants.{testVatNumber, testEmail}

import play.api.http.Status.{BAD_REQUEST, NO_CONTENT}
import play.api.libs.json.Json

object StoreEmailAddressStub extends WireMockMethods {

  def stubStoreEmailAddressSuccess(): Unit = {
    when(method = PUT, uri = s"/vat-subscription/subscription-request/vat-number/$testVatNumber/email", body = Json.obj("email" -> testEmail))
      .thenReturn(status = NO_CONTENT)
  }

  def stubStoreEmailAddressFailure(): Unit = {
    when(method = PUT, uri = s"/vat-subscription/subscription-request/vat-number/$testVatNumber/email", body = Json.obj("email" -> testEmail))
      .thenReturn(status = BAD_REQUEST)
  }

}