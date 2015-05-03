package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol._
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class BroadcastToSameProjectMembersThen(projects: Projects) {
  def apply(client: Client, pairEvent: PairEvent)(f: Client => Any) {
    def otherMembers = projects.findForClient(client).map(_.otherMembers(client)).getOrElse(Nil)
    otherMembers.foreach { otherMember =>
      def doit() {
        otherMember.writeEvent(pairEvent)
        f(otherMember)
      }
      pairEvent match {
        case _: ChangeContentEvent |
             _: CreateFileEvent | _: DeleteFileEvent | _: CreateDirEvent | _: DeleteDirEvent |
             _: RenameDirEvent | _: RenameFileEvent => doit()
        case _ if areSharingCaret(client) => doit()
        case _ =>
      }
    }
  }
  private def areSharingCaret(data: Client) = projects.findForClient(data).forall(_.isSharingCaret)
}
