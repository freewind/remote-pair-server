package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{WatchFilesChangedEvent, WatchFilesRequest}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleWatchFilesRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse, broadcastToSameProjectMembers: BroadcastToSameProjectMembers) {

  def apply(client: Client, request: WatchFilesRequest): Unit = {
    projects.findForClient(client).foreach { project =>
      project.watchFiles = request.files
      broadcastToSameProjectMembers(client, new WatchFilesChangedEvent(request.files))
      broadcastServerStatusResponse(Some(client))
    }
  }

}
