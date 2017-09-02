package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.{WatchFilesChangedEvent, WatchFilesRequest}
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleWatchFilesRequest(projects: Projects, broadcast: Broadcast) {

  def apply(client: Client, request: WatchFilesRequest): Unit = {
    projects.findForClient(client).foreach { project =>
      project.watchFiles = request.files
      broadcast.toSameProjectMembers(client, new WatchFilesChangedEvent(request.files))
      broadcast.serverStatusResponse(Some(client))
    }
  }

}
