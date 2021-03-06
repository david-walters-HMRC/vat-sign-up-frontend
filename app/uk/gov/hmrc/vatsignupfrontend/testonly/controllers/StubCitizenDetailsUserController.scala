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

package uk.gov.hmrc.vatsignupfrontend.testonly.controllers


import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.vatsignupfrontend.config.{AppConfig, ControllerComponents}
import uk.gov.hmrc.vatsignupfrontend.models.{DateModel, UserDetailsModel}
import uk.gov.hmrc.vatsignupfrontend.testonly.connectors.MatchingStubConnector
import uk.gov.hmrc.vatsignupfrontend.testonly.models.{MatchingStubFailure, MatchingStubSuccess}
import uk.gov.hmrc.vatsignupfrontend.forms.UserDetailsForm._
import uk.gov.hmrc.vatsignupfrontend.views.html.agent.client_details

import scala.concurrent.Future

//$COVERAGE-OFF$Disabling scoverage on this class as it is only intended to be used by the test only controller

@Singleton
class StubCitizenDetailsUserController @Inject()(val controllerComponents: ControllerComponents,
                                                 matchingStubConnector: MatchingStubConnector
                                                ) extends FrontendController with I18nSupport {

  val validateUserDetailsForm = userDetailsForm(isAgent = false)

  override val messagesApi: MessagesApi = controllerComponents.messagesApi

  implicit val appConfig: AppConfig = controllerComponents.appConfig

  val show: Action[AnyContent] = Action.async { implicit request =>
      Future.successful(
        Ok(client_details(validateUserDetailsForm.form.fill(UserDetailsModel("Test", "User", "AA111111A", DateModel("01","01","1980"))),
          routes.StubCitizenDetailsUserController.submit()))
      )
  }


  def submit: Action[AnyContent] = Action.async { implicit request =>
    validateUserDetailsForm.bindFromRequest.fold(
      formWithErrors =>
        Future.successful(
          BadRequest(client_details(formWithErrors, routes.StubCitizenDetailsUserController.submit()))
        ),
      userDetails =>
        matchingStubConnector.stubUser(userDetails) map {
          case Right(MatchingStubSuccess) => Ok("Successfully stubbed cid user")
          case Left(MatchingStubFailure(status)) => BadRequest(s"Failed to stub cid user with status: $status" )
        }
    )
  }

}

// $COVERAGE-ON$
