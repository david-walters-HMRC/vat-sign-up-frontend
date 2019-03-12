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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.vatsignupfrontend.SessionKeys
import uk.gov.hmrc.vatsignupfrontend.config.ControllerComponents
import uk.gov.hmrc.vatsignupfrontend.config.auth.AdministratorRolePredicate
import uk.gov.hmrc.vatsignupfrontend.config.featureswitch.ContactPreferencesJourney
import uk.gov.hmrc.vatsignupfrontend.controllers.AuthenticatedController
import uk.gov.hmrc.vatsignupfrontend.forms.ContactPreferencesForm._
import uk.gov.hmrc.vatsignupfrontend.services.StoreContactPreferenceService
import uk.gov.hmrc.vatsignupfrontend.views.html.principal.receive_email_notifications

import scala.concurrent.Future

@Singleton
class ReceiveEmailNotificationsController @Inject()(val controllerComponents: ControllerComponents,
                                                    storeContactPreferenceService: StoreContactPreferenceService
                                                   )
  extends AuthenticatedController(AdministratorRolePredicate, featureSwitches = Set(ContactPreferencesJourney)) {

  val show: Action[AnyContent] = Action.async { implicit request =>
    authorised() {

      val optEmail = request.session.get(SessionKeys.emailKey).filter(_.nonEmpty)

      optEmail match {
        case Some(email) =>
          Future.successful(
            Ok(receive_email_notifications(
              email,
              contactPreferencesForm(isAgent = false),
              routes.ReceiveEmailNotificationsController.submit()
            ))
          )
        case None =>
          Future.successful(
            Redirect(routes.CaptureEmailController.show())
          )
      }
    }
  }

  val submit: Action[AnyContent] = Action.async { implicit request =>
    authorised() {

      val optEmail = request.session.get(SessionKeys.emailKey).filter(_.nonEmpty)
      val optVatNumber = request.session.get(SessionKeys.vatNumberKey).filter(_.nonEmpty)

      contactPreferencesForm(isAgent = false).bindFromRequest.fold(
        formWithErrors => optEmail match {
          case Some(email) =>
            Future.successful(
              BadRequest(receive_email_notifications(
                email,
                formWithErrors,
                routes.ReceiveEmailNotificationsController.submit()
              ))
            )
          case None =>
            Future.successful(
              Redirect(routes.CaptureEmailController.show())
            )
        }, {
          contactPreference =>
            optVatNumber match {
              case (Some(vatNumber)) =>
                storeContactPreferenceService.storeContactPreference(vatNumber, contactPreference) map {
                  case Right(_) => Redirect(routes.TermsController.show())
                  case Left(status) => throw new InternalServerException(s"Store contact preference failed with status = $status")
                }
              case None =>
                Future.successful(
                  Redirect(routes.ResolveVatNumberController.resolve())
                )
            }
        }
      )
    }
  }
}