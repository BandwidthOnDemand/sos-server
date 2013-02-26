package nl.surfnet.sos

import scala.concurrent.stm.TMap
import scala.concurrent.stm._

object OpenSocialStore {

  val appManagerId = "bod-app-manager"
  val nocEngineerId = "noc-engineer"

  private val userMap = TMap(
    "urn:collab:person:surfguest.nl:johnsmith" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId),
      Group("UU", "UU Managers", "ict-uu"),
      Group("SARA", "SARA Managers", "ict-sara"),
      Group("Klimaat 1", "Klimaat onderzoekers", "users-klimaat"),
      Group("Klimaat 2", "Klimaat onderzokers 2", "users-klimaat2"),
      Group("BoD group", "Bandwidth On Demand Group", "bandwidth-on-demand"),
      Group("Users 1", "Users 1", "institution-users"),
      Group("Users 2", "Users 2", "institution-users2"),
      Group("Application Manager", "Application Manager", appManagerId)
    ),
    "urn:collab:person:surfnet.nl:hanst" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId)
    ),
    "urn:collab:person:surfguest.nl:selenium-user" -> Set(
      Group("NOC engineers", "NOC engineers", nocEngineerId),
      Group("Selenium ICT managers", "Selenium ICT managers", "selenium-ict-managers"),
      Group("Selenium ICT managers 2", "Selenium ICT managers 2", "selenium-ict-managers2"),
      Group("Selenium users", "Selenium users", "selenium-users"),
      Group("Selenium users 2", "Selenium users 2", "selenium-users2"),
      Group("Application Manager", "Application Manager", appManagerId)
    )
  ).single

  def users: Map[String, Set[Group]] = userMap.snapshot

  def addUser(uid: String): Unit = atomic { implicit txn =>
    userMap.put(uid, Set.empty)
  }

  def addGroup(uid: String, group: Group): Unit = atomic { implicit txn =>
    val newGroups = userMap(uid) + group
    userMap.update(uid, newGroups)
  }

  def deletePerson(uid: String): Unit = atomic { implicit txn =>
    userMap.remove(uid)
  }

  def deleteGroup(uid: String, gid: String): Unit = atomic { implicit txn =>
    val newGroups = users(uid).filterNot(g => g.id == gid)
    userMap.update(uid, newGroups)
  }

}

case class Group(title: String, description: String, id: String) {

  def groupId: String = "urn:collab:group:test.surfteams.nl:nl:surfnet:diensten:" + id

  override def equals(other: Any) = other match {
    case that: Group => this.groupId == that.groupId
    case _ => false
  }

  override def hashCode = groupId.hashCode
}