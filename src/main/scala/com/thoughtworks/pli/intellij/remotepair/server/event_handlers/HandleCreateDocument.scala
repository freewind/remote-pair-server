package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.CreateDocument
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Project}

class HandleCreateDocument(broadcastToAllMembers: BroadcastToAllMembers) {
  def apply(project: Project, client: Client, event: CreateDocument): Unit = {
    project.documents.find(event.path) match {
      case None => val doc = project.documents.create(event)
        broadcastToAllMembers(client, doc.createConfirmation())
      case Some(doc) => client.writeEvent(doc.createConfirmation())
    }
  }

}
