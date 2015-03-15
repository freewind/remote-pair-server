package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.PairableFilesRequest
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandlePairableFilesRequest(projects: Projects, broadcastServerStatusResponse: BroadcastServerStatusResponse) {
  def apply(client: Client, request: PairableFilesRequest) {
    projects.findForClient(client).foreach(_.pairableFiles = request.files)
    broadcastServerStatusResponse(Some(client))
  }

}
