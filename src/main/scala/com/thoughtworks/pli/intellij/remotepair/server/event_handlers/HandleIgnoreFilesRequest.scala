package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.IgnoreFilesRequest
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleIgnoreFilesRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, request: IgnoreFilesRequest) {
    projects.findForClient(client).foreach(_.ignoredFiles = request.files)
    broadcastServerStatusResponse(Some(client))
  }

}
