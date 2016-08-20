package controllers

import domains.user.UserInfo
import play.api.libs.json.{ JsPath, Reads }

object RequestConverters {

  implicit val UserInfoReads: Reads[UserInfo] =
    (JsPath \ "userName").read[String].map(UserInfo)
}
