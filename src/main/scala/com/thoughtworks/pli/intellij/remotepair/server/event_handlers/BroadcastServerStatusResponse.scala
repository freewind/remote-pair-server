package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{ClientInfoResponse, ProjectInfoData, ServerStatusResponse}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Clients, Project, Projects}

class BroadcastServerStatusResponse(clients: Clients, projects: Projects, sendClientInfo: SendClientInfo) {
  def apply(sourceClient: Option[Client]) {
    sourceClient.foreach(sendClientInfo.apply)

    def clientInfo(project: Project, client: Client) = ClientInfoResponse(client.id, projects.findForClient(client).get.name, client.name.get, client.isMaster)
    val event = ServerStatusResponse(
      projects = projects.all.map(p => ProjectInfoData(p.name, p.members.map(clientInfo(p, _)), p.pairableFiles, p.myWorkingMode)).toList,
      freeClients = clients.size - projects.all.flatMap(_.members).size)
    clients.all.foreach(_.writeEvent(event))
  }
}
