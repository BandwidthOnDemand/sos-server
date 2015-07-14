package nl.surfnet.sos

import java.util.UUID

object SabNgStore {

  private val userMap: Map[String, Map[UUID, (String, Set[String])]] = Map(
    "urn:collab:person:surfguest.nl:johnsmith" ->
      Map(
        UUID.fromString("ccadb9d1-0911-e511-80d0-005056956c1a") -> ("RUG" -> Set("Infraverantwoordelijke", "Instellingsbevoegde")),
        UUID.fromString("cc5ed0dd-0911-e511-80d0-005056956c1a") -> ("SARA" -> Set("Superuser"))
      ),
    "urn:collab:person:surfguest.nl:selenium-user" ->
      Map.empty,
    "urn:collab:person:surfnet.nl:hanst" ->
      Map(UUID.fromString("ad93daef-0911-e511-80d0-005056956c1a") -> ("SURFNET", Set("Infraverantwoordelijke", "Superuser")))
  )

  def getUser(nameId: String): Option[Map[UUID, (String, Set[String])]] = userMap.get(nameId)

  def users = userMap

}
