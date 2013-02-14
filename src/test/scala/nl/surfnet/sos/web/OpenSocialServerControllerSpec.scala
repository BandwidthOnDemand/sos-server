package nl.surfnet.sos.web

import org.junit.runner.RunWith
import org.scalatra.test.scalatest.ScalatraFunSuite

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class OpenSocialServerControllerSpec extends ScalatraFunSuite {

  addServlet(classOf[OpenSocialServerController], "/*")

  test("Get groups for a existing user") {
    get("/rest/groups/urn:collab:person:surfguest.nl:alanvdam") {
      status should equal (200)
    }
  }

}
