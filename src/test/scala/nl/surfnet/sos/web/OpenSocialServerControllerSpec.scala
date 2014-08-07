package nl.surfnet.sos.web

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.junit.runner.RunWith
import org.scalatra.test.scalatest.ScalatraFunSuite

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class OpenSocialServerControllerSpec extends ScalatraFunSuite {

  addServlet(classOf[OpenSocialServerController], "/*")

  test("Get groups for an existing person") {

    get(s"/groups/$johnSmithUrn") {
      status should equal (200)

      val json = parse(body)
      val JInt(total) = json \ "totalResults"
      val JArray(entries) = json \ "entry"

      total should equal (entries.size)
    }
  }

  test("Get groups for non existing person") {
    get("/groups/urn:collab:person:surfguest.nl:xxxxxx") {
      status should equal (404)
    }
  }

  test("Add a group to a person") {
    get(s"/groups/$johnSmithUrn") {
      body should not include "test-group"
    }
    post(
      s"/persons/$johnSmithUrn/groups",
      """{"title":"test-group","description":"Test Group","id":"test-group"}""",
      jsonHeaders) {

      status should equal (200)
    }
    get(s"/groups/$johnSmithUrn") {
      body should include ("test-group")
    }
  }

  test("Add group without json should fail") {
    post( s"/persons/$johnSmithUrn/groups", "", jsonHeaders) {
      status should equal (400)
    }
  }

  test("Delete group from a person") {
    get(s"/groups/$johnSmithUrn") {
      body should include ("noc-engineer")
    }
    delete(s"/persons/$johnSmithUrn/groups/$nocEngineerUrn") {
      status should equal (200)
    }
    get(s"/groups/$johnSmithUrn") {
      body should not include "noc-engineer"
    }
  }

  test("Adding a person") {
    val newPersonUrn = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:henkie"
    post("/persons", s"""{"id": "$newPersonUrn"}""", jsonHeaders) {
      status should equal (200)
    }
    get(s"/groups/$newPersonUrn") {
      status should equal (200)
    }
  }

  test("Adding a person without a json body should fail") {
    post("/persons", "") {
      status should equal (400)
    }
  }

  test("Delete a person") {
    delete(s"/persons/$johnSmithUrn") {
      status should equal (200)
    }
    get(s"/groups/$johnSmithUrn") {
      status should equal (404)
    }
  }

  val nocEngineerUrn = "noc-engineer"
  val johnSmithUrn = "urn:collab:person:surfguest.nl:johnsmith"
  val jsonHeaders = Map("Accept" -> "application/json", "Content-Type" -> "application/json")
}
