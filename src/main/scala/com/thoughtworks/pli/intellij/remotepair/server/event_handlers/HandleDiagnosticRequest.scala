package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.BuildInfo
import com.thoughtworks.pli.intellij.remotepair.protocol.DiagnosticInfo
import com.thoughtworks.pli.intellij.remotepair.server.Client

class HandleDiagnosticRequest {

  def apply(client: Client): Unit = {
    client.writeEvent(DiagnosticInfo(BuildInfo.version))
  }

}
