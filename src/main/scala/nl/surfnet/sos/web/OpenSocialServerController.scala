package nl.surfnet.sos.web

import org.scalatra.ScalatraServlet
import scala.xml.Xhtml
import net.liftweb.json._

class OpenSocialServerController extends ScalatraServlet {

  get("/") {
    Xhtml.toXhtml(<ul><li><a href="/rest/groups/@me"></a></li></ul>)
  }

  get("/rest/groups/@me") ({
    import net.liftweb.json.JsonDSL._

    contentType = "application/json"

    val groups = List(
        Group("bandwidth-on-demand", "test group"),
        Group("noc-engineer", "noc engineers"))

    compact(render(
      ("startIndex" -> 0) ~
      ("totalResults" -> groups.size) ~
      ("entry" -> groups.map {group =>
        (("id" -> ("groupId" -> group.groupId) ~ ("type" -> "groupId")) ~
         ("title" -> group.title) ~
         ("description" -> group.description)
        )})
    ))
  })
}

case class Group(title: String, description: String) {
  def groupId:String = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:" + title
}
