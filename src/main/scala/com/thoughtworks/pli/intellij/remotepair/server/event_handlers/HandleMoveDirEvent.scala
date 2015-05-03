package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.MoveDirEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

class HandleMoveDirEvent(projects: Projects, isSubPath: IsSubPath, broadcastToOtherMembers: BroadcastToOtherMembers) {
  def apply(client: Client, event: MoveDirEvent) = {
    projects.findForClient(client).foreach { project =>
      project.documents.allPaths.filter(isSubPath(_, event.path))
        .foreach(project.documents.remove)
      broadcastToOtherMembers(client, event)
    }
  }

}
