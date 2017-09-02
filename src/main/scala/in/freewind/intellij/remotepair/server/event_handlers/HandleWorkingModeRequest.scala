package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.WorkingMode
import in.freewind.intellij.remotepair.server.{Client, Project}

class HandleWorkingModeRequest(broadcast: Broadcast) {
  def apply(project: Project, newMode: WorkingMode.Value, client: Client) = {
    project.myWorkingMode = newMode
    broadcast.serverStatusResponse(Some(client))
  }

}
