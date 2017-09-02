package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol._
import in.freewind.intellij.remotepair.server.{Client, Clients, Project, Projects}

class Broadcast(clients: Clients, projects: Projects) {

  def toSameProjectMembers(client: Client, pairEvent: PairEvent): Unit = {
    projects.findForClient(client).foreach { project =>
      project.members.foreach(_.writeEvent(pairEvent))
      clients.monitors.foreach(_.writeEvent(MonitorEvent(project.name, pairEvent.toMessage)))
    }
  }

  def toSameProjectOtherMembers(client: Client, pairEvent: PairEvent): Unit = toSameProjectOtherMembersThen(client, pairEvent)(identity)

  def toSameProjectOtherMembersThen(client: Client, pairEvent: PairEvent)(f: Client => Any) {
    projects.findForClient(client).foreach { project =>
      project.otherMembersThan(client).foreach { otherMember =>
        def doit() {
          otherMember.writeEvent(pairEvent)
          f(otherMember)
        }
        pairEvent match {
          case _: ChangeContentEvent |
               _: CreateFileEvent | _: DeleteFileEvent | _: CreateDirEvent | _: DeleteDirEvent |
               _: RenameDirEvent | _: RenameFileEvent |
               _: MoveDirEvent | _: MoveFileEvent => doit()
          case _ if areSharingCaret(client) => doit()
          case _ =>
        }
      }
      clients.monitors.foreach(_.writeEvent(new MonitorEvent(project.name, pairEvent.toMessage)))
    }
  }

  def serverStatusResponse(sourceClient: Option[Client]) {
    for {
      client <- sourceClient
      project <- projects.findForClient(client)
    } client.writeEvent(clientInfo(project, client))

    def clientInfo(project: Project, client: Client) = ClientInfoResponse(client.id, project.name, client.name.get, client.isMaster)
    val event = ServerStatusResponse(
      projects = projects.all.map(p => ProjectInfoData(p.name, p.members.map(clientInfo(p, _)), p.watchFiles, p.myWorkingMode)).toList,
      freeClients = clients.size - projects.all.flatMap(_.members).size)
    clients.all.foreach(_.writeEvent(event))
  }
  private def areSharingCaret(data: Client) = projects.findForClient(data).forall(_.isSharingCaret)

}
