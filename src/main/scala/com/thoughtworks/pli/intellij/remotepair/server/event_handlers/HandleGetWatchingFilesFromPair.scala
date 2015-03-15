package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.GetWatchingFilesFromPair
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Clients}

class HandleGetWatchingFilesFromPair(clients: Clients) {
  def apply(client: Client, request: GetWatchingFilesFromPair): Unit = {
    clients.findById(request.toClientId).foreach(_.writeEvent(request))
  }

}
