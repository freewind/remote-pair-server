package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.WatchFilesRequest
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleWatchFilesRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, request: WatchFilesRequest) {
    projects.findForClient(client).foreach(_.watchFiles = request.files)
    broadcastServerStatusResponse(Some(client))
  }

}
