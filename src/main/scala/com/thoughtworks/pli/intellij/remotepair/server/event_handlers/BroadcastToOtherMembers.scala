package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent
import com.thoughtworks.pli.intellij.remotepair.server.Client

class BroadcastToOtherMembers(broadcastToSameProjectMembersThen: BroadcastToSameProjectMembersThen) {
  def apply(client: Client, pairEvent: PairEvent): Unit = broadcastToSameProjectMembersThen(client, pairEvent)(identity)
}
