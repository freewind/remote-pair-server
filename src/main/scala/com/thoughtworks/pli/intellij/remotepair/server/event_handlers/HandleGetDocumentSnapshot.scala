package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.GetDocumentSnapshot
import com.thoughtworks.pli.intellij.remotepair.server.{Clients, Project}

class HandleGetDocumentSnapshot(clients: Clients) {

  def apply(project: Project, event: GetDocumentSnapshot): Unit = {
    for {
      client <- clients.findById(event.fromClientId)
      doc <- project.documents.find(event.path)
    } {
      client.writeEvent(doc.createSnapshot())
    }
  }

}
