package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.WorkingMode
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Project}

class HandleWorkingModeRequest(broadcast: Broadcast) {
  def apply(project: Project, newMode: WorkingMode.Value, client: Client) = {
    project.myWorkingMode = newMode
    broadcast.serverStatusResponse(Some(client))
  }

}
