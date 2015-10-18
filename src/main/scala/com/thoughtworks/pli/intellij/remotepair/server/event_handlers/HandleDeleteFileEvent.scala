package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.DeleteFileEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleDeleteFileEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: DeleteFileEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.remove(event.path)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
