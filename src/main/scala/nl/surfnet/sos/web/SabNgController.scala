package nl.surfnet.sos.web

import org.scalatra._
import org.scalatra.ApiFormats._
import scala.xml.XML
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

class SabNgController extends ScalatraServlet with ApiFormats {

  private val users = Map(
    "urn:collab:person:surfguest.nl:johnsmith" ->
      Map(
        "SURFNET" -> Set("Superuser", "Instellingsbevoegde", "Infraverantwoordelijke"),
        "RUG" -> Set("Infraverantwoordelijke"),
        "SARA" -> Set("Superuser")
      ),
    "urn:collab:person:surfnet.nl:hanst" ->
      Map("SURFNET" -> Set("Infraverantwoordelijke"))
  )

  private val dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  post("/server") {
    contentType = formats("xml")

    val xmlRequest = XML.loadString(request.body)

    val nameId = (xmlRequest \\ "NameID").text
    val requestId = (xmlRequest \\ "AttributeQuery" \ "@ID").text

    val response = users.get(nameId) map { institutes =>

      <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
          <samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
            ID="_ec1dec94211fbc10dec111cc0cbefa85d6fb318dda" Version="2.0"
            IssueInstant={DateTime.now.toString(dateTimeFormatter)} InResponseTo={requestId}>
            <samlp:Status>
              <samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>
            </samlp:Status>
            { samlAssertions(institutes) }
          </samlp:Response>
        </SOAP-ENV:Body>
      </SOAP-ENV:Envelope>

    }

    response.getOrElse(<failed></failed>)
  }

  private def samlAssertions(institutes: Map[String, Set[String]]) = {

    val notBefore = DateTime.now.minusHours(2).toString(dateTimeFormatter)
    val notOnOrAfter = DateTime.now.plusHours(2).toString(dateTimeFormatter)
    val issueInstant = DateTime.now.toString(dateTimeFormatter)

    for ((institute, roles) <- institutes) yield
      <saml:Assertion xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        ID="_746aae3dc805719002b34ba54d7bd2b1a581479edc" Version="2.0" IssueInstant={issueInstant}>
        <saml:Issuer/>
        <saml:Conditions NotBefore={notBefore} NotOnOrAfter={notOnOrAfter}/>
        <saml:AttributeStatement>
          <saml:Attribute Name="urn:oid:1.3.6.1.4.1.5923.1.1.1.7" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
          {
            for (role <- roles) yield
              <saml:AttributeValue xsi:type="xs:string">{role}</saml:AttributeValue>
          }
          </saml:Attribute>
          <saml:Attribute Name="urn:oid:1.3.6.1.4.1.1076.20.100.10.50.1" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
          <saml:AttributeValue xsi:type="xs:string">{institute}</saml:AttributeValue>
        </saml:Attribute>
      </saml:AttributeStatement>
    </saml:Assertion>
  }

}