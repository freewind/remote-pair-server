package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.GetWatchingFilesFromPair
import in.freewind.intellij.remotepair.server.{Client, Clients}

class HandleGetWatchingFilesFromPair(clients: Clients) {
  def apply(client: Client, request: GetWatchingFilesFromPair): Unit = {
    clients.findById(request.toClientId).foreach(_.writeEvent(request))
  }

}
