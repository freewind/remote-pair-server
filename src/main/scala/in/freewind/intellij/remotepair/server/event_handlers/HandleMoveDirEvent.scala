package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.MoveDirEvent
import in.freewind.intellij.remotepair.server.{Client, Projects}
import in.freewind.intellij.remotepair.utils.IsSubPath

class HandleMoveDirEvent(projects: Projects, isSubPath: IsSubPath, broadcast: Broadcast) {
  def apply(client: Client, event: MoveDirEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.allPaths.filter(isSubPath(_, event.path))
        .foreach(project.documents.remove)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
