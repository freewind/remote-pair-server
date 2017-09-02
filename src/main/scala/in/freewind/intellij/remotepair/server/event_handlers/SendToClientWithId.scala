package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.PairEvent
import in.freewind.intellij.remotepair.server.Clients

class SendToClientWithId(clients: Clients) {
  def apply(event: PairEvent {val toClientId: String}): Unit = {
    clients.findById(event.toClientId) foreach (_.writeEvent(event))
  }

}
