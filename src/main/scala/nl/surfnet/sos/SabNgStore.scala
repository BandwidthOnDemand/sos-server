package nl.surfnet.sos

object SabNgStore {

  private val userMap: Map[String, Map[String, Set[String]]] = Map(
    "urn:collab:person:surfguest.nl:johnsmith" ->
      Map(
        "SURFNET" -> Set("Superuser", "Instellingsbevoegde", "Infraverantwoordelijke"),
        "RUG" -> Set("Infraverantwoordelijke"),
        "SARA" -> Set("Superuser")
      ),
    "urn:collab:person:surfguest.nl:selenium-user" ->
      Map.empty,
    "urn:collab:person:surfnet.nl:hanst" ->
      Map("SURFNET" -> Set("Infraverantwoordelijke"))
  )

  def getUser(nameId: String): Option[Map[String, Set[String]]] = userMap.get(nameId)

  def users = userMap

}