package nl.surfnet.sos
package web

import org.scalatra.ScalatraServlet
import scala.xml.Xhtml
import net.liftweb.json._
import org.scalatra.liftjson.JsonSupport
import grizzled.slf4j.Logger

class OpenSocialServerController extends ScalatraServlet with JsonSupport {

  private val logger = Logger(classOf[OpenSocialServerController])

  private val groups = Map(
      "urn:collab:person:surfguest.nl:alanvdam" -> List(
        Group("noc-engineer", "NOC engineers"),
        Group("ict-uu", "UU"),
        Group("ict-sara", "SARA"),
        Group("ict-managers", "ICT Managers X"),
        Group("users-klimaat", "Klimaat onderzoekers"),
        Group("users-klimaat2", "Klimaat onderzoekers 2"),
        Group("bandwidth-on-demand", "BoD group"),
        Group("institution-users", "Users"),
        Group("institution-users2", "Users 2")
      ),
      "urn:collab:person:surfguest.nl:okkeharsta" -> List(
        Group("bandwidth-on-demand", "test group")
      )
  )

  get("/") {
    contentType = "text/html"
    Xhtml.toXhtml(<ul><li><a href="/rest/groups/@me">/rest/groups/@me</a></li></ul>)
  }

  get("/rest/groups/:guid") {
    import net.liftweb.json.JsonDSL._

    val user = params("guid") match {
      case "@me" => params.getOrElse('xoauth_requestor_id, "anonymous")
      case guid => guid
    }

    val userGroups = groups.getOrElse(user, Nil)

    logger.info("Groups request for '%s' returning %d groups".format(user, userGroups.size))

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
