package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.{ChangeContentConfirmation, ChangeContentEvent, ServerErrorResponse}
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.StringDiff

class HandleChangeContentEvent(projects: Projects, broadcast: Broadcast) {
  def apply(client: Client, event: ChangeContentEvent) {
    projects.findForClient(client) match {
      case Some(project) => project.documents.synchronized {
        project.documents.find(event.path) match {
          case Some(doc) if event.diffs.isEmpty => project.documents.trackClientVersion(event.path, client.id, event.baseVersion)
          case Some(doc) =>
            val changes = doc.getLaterChangesFromVersion(event.baseVersion)
            val adjustedChanges = StringDiff.adjustLaterOps(changes, event.diffs)

            val editorName = client.name.get
            val newDoc = project.documents.update(doc, adjustedChanges, editorName)
            val confirm = ChangeContentConfirmation(event.eventId, event.path, newDoc.latestVersion, newDoc.latestChanges, editorName)

            project.documents.trackClientVersion(event.path, client.id, event.baseVersion)

            broadcast.toSameProjectMembers(client, confirm)
          case _ => client.writeEvent(ServerErrorResponse(s"The document of '${event.path}' is not existed on server"))
        }
      }
      case _ =>
    }
  }

}
