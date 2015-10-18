package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.SyncFilesForAll
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Projects}

class HandleSyncFilesForAll(projects: Projects) {
  def apply(client: Client): Unit = for {
    project <- projects.findForClient(client)
    master <- project.getMasterMember
    other <- project.otherMembersThan(master)
  } other.writeEvent(SyncFilesForAll)

}
