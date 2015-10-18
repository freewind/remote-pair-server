package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.DeleteDirEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

class HandleDeleteDirEvent(projects: Projects, isSubPath: IsSubPath, broadcast: Broadcast) {
  def apply(client: Client, event: DeleteDirEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.allPaths.filter(isSubPath(_, event.path))
        .foreach(project.documents.remove)
      broadcast.toSameProjectOtherMembers(client, event)
    }
  }

}
