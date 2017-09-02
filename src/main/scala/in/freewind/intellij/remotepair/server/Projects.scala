package in.freewind.intellij.remotepair.server

import in.freewind.intellij.remotepair.protocol.{ProjectOperationFailed, WorkingMode}

class Projects {
  private var map = Map.empty[String, Project]
  def all = map.values
  def singles = all.filter(_.members.size == 1)
  def contains(projectName: String): Boolean = map.contains(projectName)
  def inSameProject(user1: Client, user2: Client): Boolean = all.map(_.members).exists(m => m.contains(user1) && m.contains(user2))
  def get(projectName: String) = map.get(projectName)
  def findForClient(client: Client): Option[Project] = map.values.find(_.hasMember(client))
  def create(client: Client, projectName: String, clientName: String): Unit = {
    client.name = Some(clientName)
    client.isMaster = true
    map += (projectName -> Project(projectName, client))
  }
}

case class Project(name: String, private var member: Client) {

  @volatile var members: Seq[Client] = Seq(member)
  @volatile var watchFiles: Seq[String] = Nil
  @volatile var myWorkingMode: WorkingMode.Value = WorkingMode.CaretSharing
  val documents = new Documents(this)

  def findMemberByName(clientName: String): Option[Client] = members.find(_.name == Some(clientName))
  def hasMember(client: Client) = members.exists(_.id == client.id)
  def hasMember(clientName: String) = findMemberByName(clientName).isDefined
  def addMember(user: Client, clientName: String) {
    user.name = Some(clientName)
    members = members :+ user
    tryAutoSetMaster()
  }
  def otherMembersThan(client: Client) = members.filter(_.id != client.id)
  def removeMember(client: Client) {
    members = members.filter(_.id != client.id)
    tryAutoSetMaster()
  }
  def isEmpty = members.isEmpty

  def getMasterMember: Option[Client] = members.find(_.isMaster)
  def hasMaster: Boolean = getMasterMember.isDefined
  def setMaster(clientName: String) = {
    findMemberByName(clientName).foreach { client =>
      client.isMaster = true
      otherMembersThan(client).foreach(_.isMaster = false)
    }
  }
  def isSharingCaret = myWorkingMode == WorkingMode.CaretSharing
  private def tryAutoSetMaster(): Unit = {
    if (!hasMaster) {
      members.headOption.foreach(_.isMaster = true)
    }
  }


}
