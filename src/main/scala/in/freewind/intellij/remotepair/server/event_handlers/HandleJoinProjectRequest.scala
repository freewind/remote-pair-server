package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.{JoinedToProjectEvent, ProjectOperationFailed}
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleJoinProjectRequest(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, projectName: String, clientName: String) {
    val originalProject = projects.findForClient(client)
    projects.get(projectName) match {
      case Some(p) => {
        if (p.otherMembersThan(client).exists(_.name == Some(clientName))) {
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
          broadcast.serverStatusResponse(Some(client))
        }
      }
      case _ => client.writeEvent(ProjectOperationFailed(s"Project '$projectName' is not existed"))
    }
  }

}
