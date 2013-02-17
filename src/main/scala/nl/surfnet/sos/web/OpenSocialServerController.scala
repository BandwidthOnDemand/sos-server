package nl.surfnet.sos.web

import org.scalatra.ScalatraServlet
import org.scalatra._
import org.scalatra.json._
import org.json4s.JsonDSL._
import org.json4s._
import scala.xml.Xhtml
import grizzled.slf4j.Logger
import scala.concurrent.stm._

class OpenSocialServerController extends ScalatraServlet with JacksonJsonSupport {

  private val logger = Logger(classOf[OpenSocialServerController])

  private val appManagerId = "bod-app-manager"
  private val nocEngineerId = "noc-engineer"

  private val users = TMap(
    "urn:collab:person:surfguest.nl:johnsmith" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId),
      Group("UU", "UU Managers", "ict-uu"),
      Group("SARA", "SARA Managers", "ict-sara"),
      Group("Klimaat 1", "Klimaat onderzoekers", "users-klimaat"),
      Group("Klimaat 2", "Klimaat onderzokers 2", "users-klimaat2"),
      Group("BoD group", "Bandwidth On Demand Group", "bandwidth-on-demand"),
      Group("Users 1", "Users 1", "institution-users"),
      Group("Users 2", "Users 2", "institution-users2"),
      Group("Application Manager", "Application Manager", appManagerId)
    ),
    "urn:collab:person:surfnet.nl:hanst" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId)
    ),
    "urn:collab:person:surfguest.nl:selenium-user" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId),
      Group("Selenium ICT managers", "Selenium ICT managers", "selenium-ict-managers"),
      Group("Selenium ICT managers 2", "Selenium ICT managers 2", "selenium-ict-managers2"),
      Group("Selenium users", "Selenium users", "selenium-users"),
      Group("Selenium users 2", "Selenium users 2", "selenium-users2"),
      Group("Application Manager", "Application Manager", appManagerId)
    )
  ).single

  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/") {
    contentType = "text/html"
    Xhtml.toXhtml(
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="css/main.css" />
          <script src="/js/jquery-1.9.1.min.js"></script>
          <script src="/js/main.js"></script>
        </head>
        <body>
          <div id="messages">
          </div>
          <h1>Users</h1>
          <ul id="users">
          { for (user <- users) yield
            <li data-nameid={ user._1 }>
              <a href={"/social/rest/groups/" + user._1}>{user._1}</a>
              <ul class="buttons">
                <li>
                  { groupButton(user._2, nocEngineerId, "NOC Engineer") ++
                    groupButton(user._2, appManagerId, "App Manager") }
                </li>
                <li>{ for (group <- user._2; if group.id != nocEngineerId; if group.id != appManagerId) yield
                      groupButton(user._2, group.id, group.description)
                    }
                </li>
              </ul>
            </li>
          }
          </ul>
        </body>
      </html>
    )
  }

  def groupButton(groups: Set[Group], groupId: String, name: String) =
    <a href="#" data-roleid={ groupId } data-desc={name} class={ if(groups.map(g => g.id).contains(groupId)) "btn remove" else "btn add" }>{name}</a>

  get("/social/rest/groups/:uid") {

    users.get(params("uid")).map { userGroups =>
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

  // adds a person
  post("/persons") {
    atomic { implicit txn =>
      parsedBody match {
      case JNothing =>
        BadRequest("Wrong json provided")
      case json: JObject =>
        val JString(uid) = json \ "id"
        users.put(uid, Set.empty)
      case _ =>
        BadRequest("Wrong request")
      }
    }
  }

  // adds a group
  post("/persons/:uid/groups") {
    atomic { implicit txn =>
      parsedBody match {
        case JNothing =>
          BadRequest("Wrong json provided")
        case json: JObject =>
          val user = params("uid")
          val newGroups = users(user) + json.extract[Group]
          users.update(user, newGroups)
          Ok("message" -> "success")
        case _ =>
          BadRequest("Wrong request")
      }
    }
  }

  // deletes a person
  delete("/persons/:uid") {
    atomic { implicit txn =>
      users.remove(params("uid"))
    }
  }

  // deletes a group
  delete("/persons/:uid/groups/:gid") {
    atomic { implicit txn =>
      val newGroups = users(params("uid")).filterNot(g => g.groupId == params("gid"))
      users.update(params("uid"), newGroups)
    }
  }
}

case class Group(title: String, description: String, id: String) {

  def groupId: String = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:" + id

  override def equals(other: Any) = other match {
    case that: Group => this.groupId == that.groupId
    case _ => false
  }

  override def hashCode = groupId.hashCode
}
