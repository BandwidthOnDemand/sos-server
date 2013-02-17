package nl.surfnet.sos.web

import org.junit.runner.RunWith
import org.scalatra.test.scalatest.ScalatraFunSuite
import org.scalatra.test.scalatest.ScalatraSuite
import org.scalatest.FunSuite
import org.json4s._
import org.json4s.jackson.JsonMethods._

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class OpenSocialServerControllerSpec extends ScalatraSuite with FunSuite {

  addServlet(classOf[OpenSocialServerController], "/*")

  test("Get groups for an existing user") {

    get(s"/social/rest/groups/$johnSmithUrn") {
      status must equal (200)

      val json = parse(body)
      val JInt(total) = json \ "totalResults"
      val JArray(entries) = json \ "entry"

      total must equal (entries.size)
    }
  }

  test("Get groups for non existing user") {
    get("/social/rest/groups/urn:collab:person:surfguest.nl:xxxxxx") {
      status must equal (404)
    }
  }

  test("Add a group to an user") {
    get(s"/social/rest/groups/$johnSmithUrn") {
      body must not include ("test-group")
    }
    post(
      s"/persons/$johnSmithUrn/groups",
      """{"title":"test-group","description":"Test Group","id":"test-group"}""",
      jsonHeaders) {

      status must equal (200)
    }
    get(s"/social/rest/groups/$johnSmithUrn") {
      body must include ("test-group")
    }
  }

  test("Add group without json should fail") {
    post( s"/persons/$johnSmithUrn/groups", "", jsonHeaders) {
      status must equal (400)
    }
  }

  test("Delete group from an user") {
    get(s"/social/rest/groups/$johnSmithUrn") {
      body must include ("noc-engineer")
    }
    delete(s"/persons/$johnSmithUrn/groups/$nocEngineerUrn") {
      status must equal (200)
    }
    get(s"/social/rest/groups/$johnSmithUrn") {
      body must not include ("noc-engineer")
    }
  }

  val nocEngineerUrn = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:noc-engineer"
  val johnSmithUrn = "urn:collab:person:surfguest.nl:johnsmith"
  val jsonHeaders = Map("Accept" -> "application/json", "Content-Type" -> "application/json")
}
