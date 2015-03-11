package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.GetPairableFilesFromPair
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Clients}

class HandleGetPairableFilesFromPair(clients: Clients) {
  def apply(client: Client, event: GetPairableFilesFromPair): Unit = {
    clients.findById(event.toClientId).foreach(_.writeEvent(event))
  }

}
