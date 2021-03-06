package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.{ChangeContentConfirmation, CreateDocumentConfirmation, MonitorEvent}
import in.freewind.intellij.remotepair.server.{Projects, Client}

class HandleImMonitor(projects: Projects) {

  def apply(client: Client): Unit = {
    client.isMonitor = true
    projects.all.foreach { project =>
      project.documents.allPaths.flatMap(project.documents.find).foreach { doc =>
        client.writeEvent(new MonitorEvent(project.name, CreateDocumentConfirmation(doc.path, doc.initVersion, doc.initContent, doc.creator).toMessage))
        doc.versions
          .map(version => MonitorEvent(project.name, ChangeContentConfirmation("any", doc.path, version.version, version.diffs, version.sourceClient).toMessage))
          .foreach(client.writeEvent)
      }
    }
  }

}
