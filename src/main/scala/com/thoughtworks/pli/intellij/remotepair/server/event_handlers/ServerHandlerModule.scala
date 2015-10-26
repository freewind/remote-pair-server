package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol.ParseEvent
import com.thoughtworks.pli.intellij.remotepair.server.{Clients, Projects}
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

trait ServerHandlerModule {

  lazy val clients = new Clients
  lazy val projects = new Projects
  lazy val parseEvent = new ParseEvent
  lazy val sendClientInfo = new SendClientInfo(projects)
  lazy val broadcast = new Broadcast(clients, projects, sendClientInfo)
  lazy val handleCreateProjectRequest = new HandleCreateProjectRequest(projects, broadcast)
  lazy val handleJoinProjectRequest = new HandleJoinProjectRequest(projects, broadcast)
  lazy val handleWorkingModeRequest = new HandleWorkingModeRequest(broadcast)
  lazy val handleChangeMasterEvent = new HandleChangeMasterEvent(projects, broadcast)
  lazy val handleOpenTabEvent = new HandleOpenTabEvent(sendToMaster, broadcast, projects)
  lazy val sendToMaster = new SendToMaster(projects)
  lazy val handleChangeContentEvent = new HandleChangeContentEvent(projects)
  lazy val handleWatchFilesRequest = new HandleWatchFilesRequest(projects, broadcast)
  lazy val sendToClientWithId = new SendToClientWithId(clients)
  lazy val handleGetWatchingFilesFromPair = new HandleGetWatchingFilesFromPair(clients)
  lazy val handleCreateDocument = new HandleCreateDocument(broadcast)
  lazy val handleCreateServerDocumentRequest = new HandleCreateServerDocumentRequest(projects, sendToMaster)
  lazy val handleSyncFilesForAll = new HandleSyncFilesForAll(projects)
  lazy val handleDeleteFileEvent = new HandleDeleteFileEvent(projects, broadcast)
  lazy val isSubPath = new IsSubPath
  lazy val handleDeleteDirEvent = new HandleDeleteDirEvent(projects, isSubPath, broadcast)
  lazy val handleGetDocumentSnapshot = new HandleGetDocumentSnapshot(clients)
  lazy val handleMoveDirEvent = new HandleMoveDirEvent(projects, isSubPath, broadcast)
  lazy val handleMoveFileEvent = new HandleMoveFileEvent(projects, broadcast)
  lazy val handleEventInProject = new HandleEventInProject(handleCreateProjectRequest, handleJoinProjectRequest, handleWorkingModeRequest, handleChangeMasterEvent, handleOpenTabEvent, broadcast, sendToMaster, handleChangeContentEvent, handleWatchFilesRequest, sendToClientWithId, handleGetWatchingFilesFromPair, handleCreateDocument, handleCreateServerDocumentRequest, handleSyncFilesForAll, handleDeleteFileEvent, handleDeleteDirEvent, handleMoveDirEvent, handleMoveFileEvent, handleGetDocumentSnapshot)
  lazy val handleDiagnosticRequest = new HandleDiagnosticRequest()
  lazy val handleImMonitor = new HandleImMonitor(projects)
  lazy val serverHandlerFactory: ServerHandler.Factory = () => new ServerHandler(clients, projects, parseEvent, isSubPath, handleEventInProject, broadcast, sendToMaster, handleCreateProjectRequest, handleJoinProjectRequest, handleDiagnosticRequest, handleImMonitor)

}
