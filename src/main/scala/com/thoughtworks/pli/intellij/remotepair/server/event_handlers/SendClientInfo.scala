package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.ClientInfoResponse
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class SendClientInfo(projects: Projects) {
  def apply(client: Client) = {
    projects.findForClient(client).foreach { project =>
      client.writeEvent(ClientInfoResponse(client.id, project.name, client.name.get, client.isMaster))
    }
  }
}
