package nl.surfnet.sos.web

import org.scalatra._
import org.scalatra.json._
import org.json4s.JsonDSL._
import org.json4s._
import nl.surfnet.sos.OpenSocialStore
import nl.surfnet.sos.Group

class OpenSocialServerController extends ScalatraServlet with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/groups/:uid") {

    OpenSocialStore.users.get(params("uid")).map { userGroups =>
      val json =
        ("startIndex" -> 0) ~
        ("totalResults" -> userGroups.size) ~
        ("entry" -> userGroups.map { group =>
          ("id" -> group.groupId) ~
          ("title" -> group.title) ~
          ("description" -> group.description)
        })
      Ok(json)
    }.getOrElse(NotFound("User does not exist"))
  }

  post("/persons") {
    parsedBody match {
    case JNothing =>
      BadRequest("Wrong json provided")
    case json: JObject =>
      val JString(uid) = json \ "id"
      OpenSocialStore.addUser(uid)
    case _ =>
      BadRequest("Wrong request")
    }
  }

  post("/persons/:uid/groups") {
    parsedBody match {
      case JNothing =>
        BadRequest("Wrong json provided")
      case json: JObject =>
        OpenSocialStore.addGroup(params("uid"), json.extract[Group])
        Ok("message" -> "success")
      case _ =>
        BadRequest("Wrong request")
    }
  }

  delete("/persons/:uid") {
    OpenSocialStore.deletePerson(params("uid"))
  }

  delete("/persons/:uid/groups/:gid") {
    OpenSocialStore.deleteGroup(params("uid"), params("gid"))
  }
}