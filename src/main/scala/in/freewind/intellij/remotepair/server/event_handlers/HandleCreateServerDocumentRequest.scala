package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.CreateServerDocumentRequest
import in.freewind.intellij.remotepair.server.{Client, Projects}

class HandleCreateServerDocumentRequest(projects: Projects, sendToMaster: SendToMaster) {
  def apply(client: Client, request: CreateServerDocumentRequest) = {
    projects.findForClient(client).flatMap(_.documents.find(request.path)) match {
      case Some(doc) => client.writeEvent(doc.createConfirmation())
      case _ => sendToMaster(client, request)
    }
  }

}
