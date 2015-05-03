package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.ParseEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Clients, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

trait ServerHandlerModule {

  lazy val clients = new Clients
  lazy val projects = new Projects
  lazy val parseEvent = new ParseEvent
  lazy val sendClientInfo = new SendClientInfo(projects)
  lazy val broadcastServerStatusResponse = new BroadcastServerStatusResponse(clients, projects, sendClientInfo)
  lazy val handleCreateProjectRequest = new HandleCreateProjectRequest(projects, broadcastServerStatusResponse)
  lazy val handleJoinProjectRequest = new HandleJoinProjectRequest(projects, broadcastServerStatusResponse)
  lazy val handleWorkingModeRequest = new HandleWorkingModeRequest(broadcastServerStatusResponse)
  lazy val handleChangeMasterEvent = new HandleChangeMasterEvent(projects, broadcastServerStatusResponse)
  lazy val handleOpenTabEvent = new HandleOpenTabEvent(sendToMaster, broadcastToSameProjectMembers, projects)
  lazy val broadcastToSameProjectMembersThen = new BroadcastToSameProjectMembersThen(projects)
  lazy val broadcastToOtherMembers = new BroadcastToOtherMembers(broadcastToSameProjectMembersThen)
  lazy val sendToMaster = new SendToMaster(projects)
  lazy val handleChangeContentEvent = new HandleChangeContentEvent(projects)
  lazy val handleWatchFilesRequest = new HandleWatchFilesRequest(projects, broadcastServerStatusResponse)
  lazy val sendToClientWithId = new SendToClientWithId(clients)
  lazy val handleGetWatchingFilesFromPair = new HandleGetWatchingFilesFromPair(clients)
  lazy val broadcastToSameProjectMembers = new BroadcastToSameProjectMembers(projects)
  lazy val handleCreateDocument = new HandleCreateDocument(broadcastToSameProjectMembers)
  lazy val handleCreateServerDocumentRequest = new HandleCreateServerDocumentRequest(projects, sendToMaster)
  lazy val handleSyncFilesForAll = new HandleSyncFilesForAll(projects)
  lazy val handleDeleteFileEvent = new HandleDeleteFileEvent(projects, broadcastToOtherMembers)
  lazy val isSubPath = new IsSubPath
  lazy val handleDeleteDirEvent = new HandleDeleteDirEvent(projects, isSubPath, broadcastToOtherMembers)
  lazy val handleGetDocumentSnapshot = new HandleGetDocumentSnapshot(clients)
  lazy val handleMoveDirEvent = new HandleMoveDirEvent(projects, isSubPath, broadcastToOtherMembers)
  lazy val handleMoveFileEvent = new HandleMoveFileEvent(projects, broadcastToOtherMembers)
  lazy val handleEventInProject = new HandleEventInProject(handleCreateProjectRequest, handleJoinProjectRequest, handleWorkingModeRequest, handleChangeMasterEvent, handleOpenTabEvent, broadcastToSameProjectMembersThen, broadcastToOtherMembers, sendToMaster, handleChangeContentEvent, handleWatchFilesRequest, sendToClientWithId, handleGetWatchingFilesFromPair, handleCreateDocument, handleCreateServerDocumentRequest, handleSyncFilesForAll, handleDeleteFileEvent, handleDeleteDirEvent, handleMoveDirEvent, handleMoveFileEvent, handleGetDocumentSnapshot)
  lazy val handleDiagnosticRequest = new HandleDiagnosticRequest()
  lazy val serverHandlerFactory: ServerHandler.Factory = () => new ServerHandler(clients, projects, parseEvent, isSubPath, handleEventInProject, broadcastServerStatusResponse, broadcastToSameProjectMembersThen, sendToMaster, handleCreateProjectRequest, handleJoinProjectRequest, handleDiagnosticRequest, broadcastToOtherMembers)

}
