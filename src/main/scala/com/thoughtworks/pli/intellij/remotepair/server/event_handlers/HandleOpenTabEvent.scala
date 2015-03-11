package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{OpenTabEvent, ResetTabRequest}
import com.thoughtworks.pli.intellij.remotepair.server.Client

class HandleOpenTabEvent(sendToMaster: SendToMaster, broadcastToSameProjectMembersThen: BroadcastToSameProjectMembersThen) {
  def apply(client: Client, event: OpenTabEvent) {
    val locks = client.projectSpecifiedLocks.activeTabLocks
    locks.headOption match {
      case Some(x) if x == event.path => locks.removeHead()
      case Some(_) => sendToMaster(client, ResetTabRequest)
      case _ => broadcastToSameProjectMembersThen(client, event)(_.projectSpecifiedLocks.activeTabLocks.add(event.path))
    }
  }

}
