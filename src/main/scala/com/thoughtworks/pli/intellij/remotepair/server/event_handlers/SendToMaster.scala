package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class SendToMaster(projects: Projects) {
  def apply(client: Client, resetEvent: PairEvent) {
    projects.findForClient(client).flatMap(_.getMasterMember).foreach(_.writeEvent(resetEvent))
  }

}
