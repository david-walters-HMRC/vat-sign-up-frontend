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

package uk.gov.hmrc.vatsignupfrontend.controllers.agent

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.ControllerComponents
import uk.gov.hmrc.vatsignupfrontend.config.auth.AgentEnrolmentPredicate
import uk.gov.hmrc.vatsignupfrontend.controllers.AuthenticatedController
import uk.gov.hmrc.vatsignupfrontend.forms.CompanyNumberForm._
import uk.gov.hmrc.vatsignupfrontend.forms.validation.utils.Patterns.CompanyNumber
import uk.gov.hmrc.vatsignupfrontend.httpparsers.GetCompanyNameHttpParser.{CompanyNumberNotFound, GetCompanyNameFailureResponse, GetCompanyNameSuccess}
import uk.gov.hmrc.vatsignupfrontend.services.GetCompanyNameService
import uk.gov.hmrc.vatsignupfrontend.views.html.agent.capture_company_number

import scala.concurrent.Future

@Singleton
class CaptureRegisteredSocietyCompanyNumberController @Inject()(val controllerComponents: ControllerComponents,
                                                                val getCompanyNameService: GetCompanyNameService
                                              ) extends AuthenticatedController(AgentEnrolmentPredicate) {

  val validateCompanyNumberForm = companyNumberForm(isAgent = true, isPartnership = false)

  def validateCrnPrefix(companyNumber: String): Boolean = {
    companyNumber match {
      case CompanyNumber.allNumbersRegex(numbers) if numbers.toInt > 0 => true
      case CompanyNumber.withPrefixRegex(prefix, numbers) if CompanyNumber.validCompanyNumberPrefixes.contains(prefix) && numbers.toInt > 0 => true
      case _ => false
    }
  }

  val show: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        Future.successful(
          Ok(capture_company_number(validateCompanyNumberForm.form, routes.CaptureRegisteredSocietyCompanyNumberController.submit()))
        )
      }
  }

  val submit: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        validateCompanyNumberForm.bindFromRequest.fold(
          formWithErrors =>
            Future.successful(
              BadRequest(capture_company_number(formWithErrors, routes.CaptureCompanyNumberController.submit()))
            ),
          companyNumber =>
            if (validateCrnPrefix(companyNumber)) {
              getCompanyNameService.getCompanyName(companyNumber) map {
                case Right(GetCompanyNameSuccess(companyName, _)) =>
                  NotImplemented // TODO Redirect to Confirm Registered Society Name Controller
                    .addingToSession(
                      SessionKeys.societyCompanyNumberKey -> companyNumber,
                      SessionKeys.societyNameKey -> companyName
                    )
                case Left(CompanyNumberNotFound) =>
                  Redirect(routes.CompanyNameNotFoundController.show())
                case Left(GetCompanyNameFailureResponse(status)) =>
                  throw new InternalServerException(s"getCompanyName failed: status=$status")
              }
            } else {
              Future.successful(
                Redirect(routes.CompanyNameNotFoundController.show())
              )
            }
        )
      }
  }
}