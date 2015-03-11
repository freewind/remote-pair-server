package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.softwaremill.macwire.Macwire
import com.thoughtworks.pli.intellij.remotepair.protocol.ParseEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Clients, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

trait ServerHandlerModule extends Macwire {

  lazy val clients = wire[Clients]
  lazy val projects = wire[Projects]
  lazy val parseEvent = wire[ParseEvent]
  lazy val sendClientInfo = wire[SendClientInfo]
  lazy val broadcastServerStatusResponse = wire[BroadcastServerStatusResponse]
  lazy val handleCreateProjectRequest = wire[HandleCreateProjectRequest]
  lazy val handleJoinProjectRequest = wire[HandleJoinProjectRequest]
  lazy val handleWorkingModeRequest = wire[HandleWorkingModeRequest]
  lazy val handleChangeMasterEvent = wire[HandleChangeMasterEvent]
  lazy val handleOpenTabEvent = wire[HandleOpenTabEvent]
  lazy val broadcastToSameProjectMembersThen = wire[BroadcastToSameProjectMembersThen]
  lazy val broadcastToOtherMembers = wire[BroadcastToOtherMembers]
  lazy val handleResetTabEvent = wire[HandleResetTabEvent]
  lazy val sendToMaster = wire[SendToMaster]
  lazy val handleChangeContentEvent = wire[HandleChangeContentEvent]
  lazy val handleIgnoreFilesRequest = wire[HandleIgnoreFilesRequest]
  lazy val sendToClientWithId = wire[SendToClientWithId]
  lazy val handleGetPairableFilesFromPair = wire[HandleGetPairableFilesFromPair]
  lazy val broadcastToAllMembers = wire[BroadcastToAllMembers]
  lazy val handleCreateDocument = wire[HandleCreateDocument]
  lazy val handleCreateServerDocumentRequest = wire[HandleCreateServerDocumentRequest]
  lazy val handleSyncFilesForAll = wire[HandleSyncFilesForAll]
  lazy val handleDeleteFileEvent = wire[HandleDeleteFileEvent]
  lazy val isSubPath = wire[IsSubPath]
  lazy val handleDeleteDirEvent = wire[HandleDeleteDirEvent]
  lazy val handleEventInProject = wire[HandleEventInProject]
  lazy val serverHandler = wire[ServerHandler]

}
