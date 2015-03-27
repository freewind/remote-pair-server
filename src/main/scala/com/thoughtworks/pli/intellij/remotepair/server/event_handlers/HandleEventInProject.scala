package com.thoughtworks.pli.intellij.remotepair.server.event_handlers

import com.thoughtworks.pli.intellij.remotepair.protocol._
import com.thoughtworks.pli.intellij.remotepair.server.{Client, Project}

class HandleEventInProject(handleCreateProjectRequest: HandleCreateProjectRequest,
                           handleJoinProjectRequest: HandleJoinProjectRequest,
                           handleWorkingModeRequest: HandleWorkingModeRequest,
                           handleChangeMasterEvent: HandleChangeMasterEvent,
                           handleOpenTabEvent: HandleOpenTabEvent,
                           broadcastToSameProjectMembersThen: BroadcastToSameProjectMembersThen,
                           broadcastToOtherMembers: BroadcastToOtherMembers,
                           sendToMaster: SendToMaster,
                           handleChangeContentEvent: HandleChangeContentEvent,
                           handleWatchFilesRequest: HandleWatchFilesRequest,
                           sendToClientWithId: SendToClientWithId,
                           handleGetWatchingFilesFromPair: HandleGetWatchingFilesFromPair,
                           handleCreateDocument: HandleCreateDocument,
                           handleCreateServerDocumentRequest: HandleCreateServerDocumentRequest,
                           handleSyncFilesForAll: HandleSyncFilesForAll,
                           handleDeleteFileEvent: HandleDeleteFileEvent,
                           handleDeleteDirEvent: HandleDeleteDirEvent) {

  def apply(project: Project, event: PairEvent, client: Client) = event match {
    case CreateProjectRequest(projectName, clientName) => handleCreateProjectRequest(client, projectName, clientName)
    case JoinProjectRequest(projectName, clientName) => handleJoinProjectRequest(client, projectName, clientName)
    case CaretSharingModeRequest => handleWorkingModeRequest(project, WorkingMode.CaretSharing, client)
    case ParallelModeRequest => handleWorkingModeRequest(project, WorkingMode.Parallel, client)
    case event: ChangeMasterRequest => handleChangeMasterEvent(client, event)
    case event: OpenTabEvent => handleOpenTabEvent(client, event)
    case event: CloseTabEvent => broadcastToOtherMembers(client, event)
    case event: ChangeContentEvent => handleChangeContentEvent(client, event)
    case event: MoveCaretEvent => broadcastToOtherMembers(client, event)
    case event: WatchFilesRequest => handleWatchFilesRequest(client, event)
    case req: SyncFilesRequest => sendToMaster(client, req)
    case event: MasterWatchingFiles => sendToClientWithId(event)
    case event: SyncFileEvent => sendToClientWithId(event)
    case event: WatchingFiles => sendToClientWithId(event)
    case request: GetWatchingFilesFromPair => handleGetWatchingFilesFromPair(client, request)
    case event: CreateDocument => handleCreateDocument(project, client, event)
    case request: CreateServerDocumentRequest => handleCreateServerDocumentRequest(client, request)
    case SyncFilesForAll => handleSyncFilesForAll(client)
    case event: DeleteFileEvent => handleDeleteFileEvent(client, event)
    case event: DeleteDirEvent => handleDeleteDirEvent(client, event)
    case _ => broadcastToOtherMembers(client, event)
  }

}
