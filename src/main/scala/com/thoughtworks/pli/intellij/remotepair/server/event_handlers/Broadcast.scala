package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol._
import com.thoughtworks.pli.intellij.remotepair.server.{Projects, Clients, Project, Client}

class Broadcast(clients: Clients, projects: Projects, sendClientInfo: SendClientInfo) {

  def toSameProjectMembers(client: Client, pairEvent: PairEvent): Unit = {
    projects.findForClient(client).map(_.members).foreach { members =>
      members.foreach(m => m.writeEvent(pairEvent))
    }
  }

  def toSameProjectOtherMembers(client: Client, pairEvent: PairEvent): Unit = toSameProjectOtherMembersThen(client, pairEvent)(identity)

  def toSameProjectOtherMembersThen(client: Client, pairEvent: PairEvent)(f: Client => Any) {
    def otherMembers = projects.findForClient(client).map(_.otherMembersThan(client)).getOrElse(Nil)
    otherMembers.foreach { otherMember =>
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
  }

  def serverStatusResponse(sourceClient: Option[Client]) {
    sourceClient.foreach(sendClientInfo.apply)

    def clientInfo(project: Project, client: Client) = ClientInfoResponse(client.id, projects.findForClient(client).get.name, client.name.get, client.isMaster)
    val event = ServerStatusResponse(
      projects = projects.all.map(p => ProjectInfoData(p.name, p.members.map(clientInfo(p, _)), p.watchFiles, p.myWorkingMode)).toList,
      freeClients = clients.size - projects.all.flatMap(_.members).size)
    clients.all.foreach(_.writeEvent(event))
  }
  private def areSharingCaret(data: Client) = projects.findForClient(data).forall(_.isSharingCaret)

}
