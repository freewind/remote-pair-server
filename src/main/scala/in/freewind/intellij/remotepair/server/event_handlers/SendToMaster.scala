package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair.protocol.PairEvent
import in.freewind.intellij.remotepair.server.{Client, Projects}

class SendToMaster(projects: Projects) {
  def apply(client: Client, resetEvent: PairEvent) {
    projects.findForClient(client).flatMap(_.getMasterMember).foreach(_.writeEvent(resetEvent))
  }

}
