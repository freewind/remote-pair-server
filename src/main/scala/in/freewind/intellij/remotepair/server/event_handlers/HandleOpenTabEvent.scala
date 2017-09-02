package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.OpenTabEvent
import in.freewind.intellij.remotepair.server.{Projects, Client}

class HandleOpenTabEvent(sendToMaster: SendToMaster, broadcast:Broadcast, projects: Projects) {

  def apply(client: Client, event: OpenTabEvent): Unit = {
    if (projects.findForClient(client).exists(_.isSharingCaret)) {
      broadcast.toSameProjectMembers(client, event)
    }
  }

}
