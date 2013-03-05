package nl.surfnet.sos

object SabNgStore {

  private val userMap: Map[String, Map[String, Set[String]]] = Map(
    "urn:collab:person:surfguest.nl:johnsmith" ->
      Map(
        "RUG" -> Set("Infraverantwoordelijke", "Instellingsbevoegde"),
        "SARA" -> Set("Superuser")
      ),
    "urn:collab:person:surfguest.nl:selenium-user" ->
      Map.empty,
    "urn:collab:person:surfnet.nl:hanst" ->
      Map("SURFNET" -> Set("Infraverantwoordelijke", "Superuser"))
  )

  def getUser(nameId: String): Option[Map[String, Set[String]]] = userMap.get(nameId)

  def users = userMap

}
