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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.{InternalServerException, NotFoundException}
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.ControllerComponents
import uk.gov.hmrc.vatsignupfrontend.config.auth.AdministratorRolePredicate
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.{GeneralPartnershipJourney, LimitedPartnershipJourney}
import uk.gov.hmrc.vatsignupfrontend.controllers.AuthenticatedController
import uk.gov.hmrc.vatsignupfrontend.forms.PartnershipUtrForm.partnershipUtrForm
import uk.gov.hmrc.vatsignupfrontend.views.html.principal.partnerships.capture_partnership_utr

import scala.concurrent.Future

@Singleton
class CapturePartnershipUtrController @Inject()(val controllerComponents: ControllerComponents)
  extends AuthenticatedController(
    AdministratorRolePredicate,
    featureSwitches = Set(GeneralPartnershipJourney, LimitedPartnershipJourney)
  ) {

  override def featureEnabled[T](func: => T): T =
    if (featureSwitches exists isEnabled) func
    else throw new NotFoundException(featureSwitchError)


  val show: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        Future.successful(
          Ok(capture_partnership_utr(
            partnershipUtrForm.form,
            routes.CapturePartnershipUtrController.submit())
          )
        )
      }
  }

  val submit: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        partnershipUtrForm.form.bindFromRequest.fold(
          formWithErrors =>
            Future.successful(
              BadRequest(capture_partnership_utr(
                formWithErrors,
                routes.CapturePartnershipUtrController.submit()
              ))
            ),
          utr =>
            Future.successful(
              Redirect(routes.PrincipalPlacePostCodeController.show())
              .addingToSession(SessionKeys.partnershipSautrKey -> utr))
        )
      }
  }
}