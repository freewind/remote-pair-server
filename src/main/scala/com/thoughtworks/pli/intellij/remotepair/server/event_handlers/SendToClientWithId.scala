package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent
import com.thoughtworks.pli.intellij.remotepair.server.Clients

class SendToClientWithId(clients: Clients) {
  def apply(event: PairEvent {val toClientId: String}): Unit = {
    clients.findById(event.toClientId) foreach (_.writeEvent(event))
  }

}
