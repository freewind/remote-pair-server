package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.{CreatedProjectEvent, ProjectOperationFailed}
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleCreateProjectRequest(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, projectName: String, clientName: String) {
    if (projects.contains(projectName)) {
      client.writeEvent(ProjectOperationFailed(s"Project '$projectName' is already existed"))
    } else {
      projects.findForClient(client) match {
        case Some(p) => p.removeMember(client)
        case _ =>
      }
      projects.create(client, projectName, clientName)
      client.writeEvent(CreatedProjectEvent(projectName, clientName))
      broadcast.serverStatusResponse(Some(client))
    }
  }

}
