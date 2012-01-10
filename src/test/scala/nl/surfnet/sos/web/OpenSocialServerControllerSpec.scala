package nl.surfnet.sos
package web

import org.junit.runner.RunWith
import org.scalatra.test.scalatest.ScalatraFunSuite

import net.liftweb.json.JValue
import net.liftweb.json.parse
import net.liftweb.json.DefaultFormats

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class OpenSocialServerControllerSpec extends ScalatraFunSuite {
  implicit val formats = DefaultFormats

  addServlet(classOf[OpenSocialServerController], "/*")

  test("Get groups with missing requestor id should give 0 results") {
    get("/rest/groups/@me") {
      status should equal (200)

      val jsonResult = parse(body).extract[JsonResult]

      jsonResult.entry.size should be (0)
      jsonResult.totalResults should be (0)
    }
  }

  test("Get groups with a requestor id should give results") {
    get("/rest/groups/@me?xoauth_requestor_id=urn:collab:person:surfguest.nl:alanvdam") {
      status should equal (200)

      val jsonResult = parseResult(body)

      jsonResult.totalResults should be > 0
      jsonResult.entry.size should be (jsonResult.totalResults)
    }
  }

  test("Get groups for a existing user") {
    get("/rest/groups/urn:collab:person:surfguest.nl:alanvdam") {
      status should equal (200)

      val jsonResult = parseResult(body)

      jsonResult.totalResults should be > 0
      jsonResult.entry.size should be (jsonResult.totalResults)
    }
  }

  private def parseResult(jsonString: String): JsonResult = parse(jsonString).extract[JsonResult]

  case class JsonResult(startIndex: Int, totalResults: Int, entry: List[JValue])
}