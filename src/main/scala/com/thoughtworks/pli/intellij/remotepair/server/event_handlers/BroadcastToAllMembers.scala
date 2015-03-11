package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class BroadcastToAllMembers(projects: Projects) {
  def apply(client: Client, pairEvent: PairEvent): Unit = {
    projects.findForClient(client).map(_.members).foreach { members =>
      members.foreach(m => m.writeEvent(pairEvent))
    }
  }

}
