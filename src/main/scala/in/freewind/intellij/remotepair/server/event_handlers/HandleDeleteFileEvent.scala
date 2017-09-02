package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.DeleteFileEvent
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleDeleteFileEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: DeleteFileEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.remove(event.path)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
