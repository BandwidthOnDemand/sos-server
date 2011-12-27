package nl.surfnet.sos.web

import org.scalatra.ScalatraServlet
import scala.xml.Xhtml
import net.liftweb.json._
import org.scalatra.liftjson.JsonSupport

class OpenSocialServerController extends ScalatraServlet with JsonSupport {

  val groups = Map(
      "urn:collab:person:surfguest.nl:alanvdam" -> List(
        Group("bandwidth-on-demand", "test group"),
        Group("institution-users", "users"),
        Group("institution-users2", "users"),
        Group("noc-engineer", "noc engineers"))
      )

  get("/") {
    contentType = "text/html"
    Xhtml.toXhtml(<ul><li><a href="/rest/groups/@me">/rest/groups/@me</a></li></ul>)
  }

  get("/rest/groups/@me") {
    import net.liftweb.json.JsonDSL._

    val user = params.getOrElse('xoauth_requestor_id, "anonymous")
    val userGroups = groups.getOrElse(user, Nil)

    ("startIndex" -> 0) ~
    ("totalResults" -> userGroups.size) ~
    ("entry" -> userGroups.map {group =>
      (("id" -> ("groupId" -> group.groupId) ~ ("type" -> "groupId")) ~
       ("title" -> group.title) ~
       ("description" -> group.description))
    })
  }
}

case class Group(title: String, description: String) {
  def groupId:String = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:" + title
}
