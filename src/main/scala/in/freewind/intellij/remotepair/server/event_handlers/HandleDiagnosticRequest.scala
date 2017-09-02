package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.BuildInfo
import in.freewind.intellij.remotepair.protocol.DiagnosticInfo
import in.freewind.intellij.remotepair.server.Client

class HandleDiagnosticRequest {

  def apply(client: Client): Unit = {
    client.writeEvent(DiagnosticInfo(BuildInfo.version))
  }

}
