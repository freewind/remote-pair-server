package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.ResetTabEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleResetTabEvent(projects: Projects, broadcastToSameProjectMembersThen: BroadcastToSameProjectMembersThen) {
  def apply(client: Client, event: ResetTabEvent) {
    projects.findForClient(client).foreach(_.members.foreach(_.projectSpecifiedLocks.activeTabLocks.clear()))
    broadcastToSameProjectMembersThen(client, event)(_.projectSpecifiedLocks.activeTabLocks.add(event.path))
  }

}
