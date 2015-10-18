package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.OpenTabEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Projects, Client}

class HandleOpenTabEvent(sendToMaster: SendToMaster, broadcast:Broadcast, projects: Projects) {

  def apply(client: Client, event: OpenTabEvent): Unit = {
    if (projects.findForClient(client).exists(_.isSharingCaret)) {
      broadcast.toSameProjectMembers(client, event)
    }
  }

}
