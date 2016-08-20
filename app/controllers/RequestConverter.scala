package controllers

import domains.UserInfo
import play.api.libs.json.{ JsPath, Reads }

object RequestConverter {

  implicit val UserInfoReads: Reads[UserInfo] =
    (JsPath \ "userName").read[String].map(UserInfo)
}
