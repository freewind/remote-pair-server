package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{ChangeMasterRequest, ServerErrorResponse}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleChangeMasterEvent(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, event: ChangeMasterRequest) {
    projects.findForClient(client).foreach { project =>
      if (project.hasMember(event.clientName)) {
        project.setMaster(event.clientName)
        broadcastServerStatusResponse(Some(client))
      } else {
        client.writeEvent(ServerErrorResponse(s"Specified user '${event.clientName}' is not found"))
      }
    }
  }

}