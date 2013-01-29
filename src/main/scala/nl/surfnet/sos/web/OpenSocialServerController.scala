package nl.surfnet.sos
package web

import org.scalatra.ScalatraServlet
import scala.xml.Xhtml
import net.liftweb.json._
import org.scalatra.liftjson.LiftJsonSupport
import grizzled.slf4j.Logger
import scala.concurrent.stm._
import scala.util.parsing.json.JSON

class OpenSocialServerController extends ScalatraServlet with LiftJsonSupport {

  private val logger = Logger(classOf[OpenSocialServerController])

  private val groups = TMap(
    "urn:collab:person:surfguest.nl:johnsmith" -> List(
       Group("noc-engineer", "NOC engineers")
     , Group("ict-uu", "UU")
     , Group("ict-sara", "SARA")
     , Group("ict-managers", "ICT Managers X")
     , Group("users-klimaat", "Klimaat onderzoekers")
     , Group("users klimaat 2", "Klimaat onderzoekers 2", Some("users-klimaat2"))
     , Group("bandwidth-on-demand", "BoD group")
     , Group("institution-users", "Users")
     , Group("institution-users2", "Users 2")
     , Group("bod-app-mananger", "Application Manager")
    ),
    "urn:collab:person:surfnet.nl:hanst" -> List(
      Group("noc-engineer", "NOC engineers")
    ),
    "urn:henk" -> List(
      Group("institution-users3", "Users 3")
    , Group("institution-users4", "Users 4")
    ),
    "urn:collab:person:surfguest.nl:selenium-user" -> List(
      Group("noc-engineer", "NOC engineers")
    , Group("selenium-ict-managers", "Selenium ICT managers")
    , Group("selenium-ict-managers2", "Selenium ICT managers 2")
    , Group("selenium-users", "Selenium users")
    , Group("selenium-users2", "Selenium users 2")
    , Group("bod-app-mananger", "Application Manager")
    )
  ).single

  get("/") {
    contentType = "text/html"
    Xhtml.toXhtml(<ul><li><a href="/social/rest/groups/@me">/social/rest/groups/@me</a></li></ul>)
  }

  get("/social/rest/groups/:guid") {
    import net.liftweb.json.JsonDSL._

    val user = params("guid") match {
      case "@me" => params.getOrElse('xoauth_requestor_id, "anonymous")
      case guid => guid
    }

    val userGroups = groups.getOrElse(user, Nil)

    logger.debug("Groups request for '%s' returning %d groups".format(user, userGroups.size))

    ("startIndex" -> 0) ~
      ("totalResults" -> userGroups.size) ~
      ("entry" -> userGroups.map { group =>
        (("id" -> group.groupId) ~
          ("title" -> group.title) ~
          ("description" -> group.description))
      })
  }

  // add a person
  post("/persons") {
    val person = JSON.parseFull(request.body).get.asInstanceOf[Map[String, String]]
    groups.put(person("id"), Nil)
  }

  // add a group
  post("/persons/:uid/groups") {
    atomic { implicit txn =>
      val group = JSON.parseFull(request.body).get.asInstanceOf[Map[String, String]]
      val newGroups = Group(group("title"), group.getOrElse("discription", "")) :: groups(params("uid"))
      groups.update(params("uid"), newGroups)
    }
  }

  // delete a person
  delete("/persons/:uid") {
    atomic { implicit txn =>
      groups.remove(params("uid"))
    }
  }

  // delete a group
  delete("/persons/:uid/groups/:guid") {
    val newGroups = groups(params("uid")).filterNot(group => group.groupId == params("guid"))
    groups.update(params("uid"), newGroups)
  }

}

case class Group(title: String, description: String, id: Option[String] = None) {

  def groupId: String = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:" + id.getOrElse(title)
}
