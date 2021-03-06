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

package uk.gov.hmrc.vatsignupfrontend.controllers.principal

import play.api.http.Status._
import uk.gov.hmrc.vatsignupfrontend.forms.BusinessEntityForm
import uk.gov.hmrc.vatsignupfrontend.forms.BusinessEntityForm._
import uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks.AuthStub._
import uk.gov.hmrc.vatsignupfrontend.helpers.{ComponentSpecBase, CustomMatchers}

class CaptureBusinessEntityControllerISpec extends ComponentSpecBase with CustomMatchers {
  "GET /business-type" should {
    "return an OK" in {
      stubAuth(OK, successfulAuthResponse())

      val res = get("/business-type")

      res should have(
        httpStatus(OK)
      )
    }
  }

  "POST /business-type" when {
    "the business type is sole trader" should {
      "return a SEE_OTHER status and go to the Sole Trader resolver" in {
        stubAuth(OK, successfulAuthResponse(vatDecEnrolment))

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> soleTrader)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(soletrader.routes.SoleTraderResolverController.resolve().url)
        )
      }
    }

    "the business type is limited company" when {
      "the user has VAT-DEC enrolment" should {
        "return a SEE_OTHER status and go to capture company number" in {
          stubAuth(OK, successfulAuthResponse(vatDecEnrolment))

          val res = post("/business-type")(BusinessEntityForm.businessEntity -> limitedCompany)

          res should have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.CaptureCompanyNumberController.show().url)
          )
        }
      }
      "the user does not have a VAT-DEC enrolment" should {
        "return a SEE_OTHER status and go to capture company number" in {
          stubAuth(OK, successfulAuthResponse())

          val res = post("/business-type")(BusinessEntityForm.businessEntity -> limitedCompany)

          res should have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.CaptureCompanyNumberController.show().url)
          )
        }
      }
    }

    "the business type is general partnership" should {
      "return a SEE_OTHER status and go to resolve partnership utr controller" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> generalPartnership)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(partnerships.routes.ResolvePartnershipUtrController.resolve().url)
        )
      }
    }

    "the business type is limited partnership" should {
      "return a SEE_OTHER status and go to capture partnership company number" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> limitedPartnership)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(partnerships.routes.CapturePartnershipCompanyNumberController.show().url)
        )
      }
    }

    "the business type is vat group" should {
      "return a SEE_OTHER status and go to vat group" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> vatGroup)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.VatGroupResolverController.resolve().url)
        )
      }
    }

    "the business type is division" should {
      "return a SEE_OTHER status and go to division resolver" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> division)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.DivisionResolverController.resolve().url)
        )
      }
    }

    "the business type is unincorporated association" should {
      "return a SEE_OTHER status and go to unincorporated association resolver" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> unincorporatedAssociation)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.UnincorporatedAssociationResolverController.resolve().url)
        )
      }
    }

    "the business type is trust" should {
      "return a SEE_OTHER status and go to trust resolver" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> trust)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.TrustResolverController.resolve().url)
        )
      }
    }

    "the business type is registered society" should {
      "return a SEE_OTHER status and go to capture society company number page" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> registeredSociety)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.CaptureRegisteredSocietyCompanyNumberController.show().url)
        )
      }
    }

    "the business type is a charity" should {
      "return a SEE_OTHER status and go to charity resolver" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> charity)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.CharityResolverController.resolve().url)
        )
      }
    }

    "the business type is a government organisation" should {
      "return a SEE_OTHER status and go to government organisation resolver" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> governmentOrganisation)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.GovernmentOrganisationResolverController.resolve().url)
        )
      }
    }

    "the business type is other" should {
      "return a SEE_OTHER status and go to cannot use service" in {
        stubAuth(OK, successfulAuthResponse())

        val res = post("/business-type")(BusinessEntityForm.businessEntity -> other)

        res should have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.CannotUseServiceController.show().url)
        )
      }
    }

  }

}
