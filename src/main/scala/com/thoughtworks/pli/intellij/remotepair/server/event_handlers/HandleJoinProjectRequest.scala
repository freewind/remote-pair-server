package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{JoinedToProjectEvent, ProjectOperationFailed}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleJoinProjectRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, projectName: String, clientName: String) {
    val originalProject = projects.findForClient(client)
    projects.get(projectName) match {
      case Some(p) => {
        if (p.otherMembers(client).exists(_.name == Some(clientName))) {
          client.writeEvent(ProjectOperationFailed(s"The client name '$clientName' is already used in project '$projectName'"))
        } else {
          if (p.hasMember(client)) {
            client.name = Some(clientName)
          } else {
            p.addMember(client, clientName)
          }
          if (originalProject.isDefined && originalProject != Some(p)) {
            originalProject.foreach(_.removeMember(client))
          }
          client.writeEvent(JoinedToProjectEvent(projectName, clientName))
          broadcastServerStatusResponse(Some(client))
        }
      }
      case _ => client.writeEvent(ProjectOperationFailed(s"Project '$projectName' is not existed"))
    }
  }

}
