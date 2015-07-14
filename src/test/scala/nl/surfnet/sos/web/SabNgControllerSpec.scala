package nl.surfnet.sos.web

import org.junit.runner.RunWith
import org.scalatra.test.scalatest.ScalatraFunSuite

import scala.xml.XML

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class SabNgControllerSpec extends ScalatraFunSuite {

  addServlet(classOf[SabNgController], "/*")

  test("get saml assertions for john") {

    val dummyRequest =
      <dummyrequest>
        <NameID>urn:collab:person:surfguest.nl:johnsmith</NameID>
        <AttributeQuery ID="1234567890" />
      </dummyrequest>

    post("/server", dummyRequest.toString()) {

      val xmlResponse = XML.loadString(body)

      xmlResponse \\ "AttributeStatement" should have length 2

      (xmlResponse \\ "Response" \ "@InResponseTo").text should equal ("1234567890")

      status should equal (200)
    }
  }

}
