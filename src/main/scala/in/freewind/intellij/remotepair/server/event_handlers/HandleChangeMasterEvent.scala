package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.{ChangeMasterRequest, ServerErrorResponse}
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleChangeMasterEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: ChangeMasterRequest) {
    projects.findForClient(client).foreach { project =>
      if (project.hasMember(event.clientName)) {
        project.setMaster(event.clientName)
        broadcast.serverStatusResponse(Some(client))
      } else {
        client.writeEvent(ServerErrorResponse(s"Specified user '${event.clientName}' is not found"))
      }
    }
  }

}
