package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.MoveFileEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleMoveFileEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: MoveFileEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.remove(event.path)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
