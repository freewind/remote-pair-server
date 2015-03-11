package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{JoinedToProjectEvent, ProjectOperationFailed}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleCreateProjectRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, projectName: String, clientName: String) {
    if (projects.contains(projectName)) {
      client.writeEvent(ProjectOperationFailed(s"Project '$projectName' is already existed"))
    } else {
      projects.findForClient(client) match {
        case Some(p) => p.removeMember(client)
        case _ =>
      }
      projects.create(client, projectName, clientName)
      client.writeEvent(JoinedToProjectEvent(projectName, clientName))
      broadcastServerStatusResponse(Some(client))
    }
  }

}
