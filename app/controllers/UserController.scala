package controllers

import javax.inject.Inject

import core.util.FutureSupport
import domains.error.RegisterConflictError
import play.api.libs.json.Json
import play.api.mvc.{ Action, BodyParsers, Controller }
import services.user.UserRegisterService

import scala.concurrent.ExecutionContext.Implicits.global

class UserController @Inject()(
    registerService: UserRegisterService
) extends Controller {

  import RequestConverter._

  def register = Action.async(BodyParsers.parse.json) { req =>
    val result = for {
      userName <- FutureSupport.jsResultToFuture(req.body.validate[String])
      (sessionId, userPoint) <- registerService.register(userName)
    } yield Created(Json.obj("sessionId" -> sessionId, "userPoint" -> userPoint))
    result recover {
      case _: RegisterConflictError => Conflict("register process in progress.")
    }
  }
}
