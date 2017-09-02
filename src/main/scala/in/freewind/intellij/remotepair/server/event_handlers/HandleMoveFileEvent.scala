package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.MoveFileEvent
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleMoveFileEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: MoveFileEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.remove(event.path)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
