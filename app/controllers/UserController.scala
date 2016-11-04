package controllers

import javax.inject.Inject

import core.util.FutureSupport
import domains.error.RegisterConflictError
import domains.user.UserInfo
import play.api.libs.json.Json
import play.api.mvc.{ Action, BodyParsers, Controller }
import services.user.UserRegisterService

import scala.concurrent.ExecutionContext.Implicits.global

class UserController @Inject()(
    registerService: UserRegisterService
) extends Controller {

  import RequestConverters._

  def register = Action.async(BodyParsers.parse.json) { req =>
    val result = for {
      user <- FutureSupport.jsResultToFuture(req.body.validate[UserInfo])
      (sessionId, userPoint) <- registerService.register(user.userName)
    } yield Created(Json.obj("sessionId" -> sessionId, "userPoint" -> userPoint))
    result recover {
      case _: RegisterConflictError => Conflict("register process in progress.")
    }
  }
}
