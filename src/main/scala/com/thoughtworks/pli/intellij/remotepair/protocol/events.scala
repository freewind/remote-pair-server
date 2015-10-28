package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.utils.ContentDiff

case class InvalidOperationState(message: String) extends PairEvent
case object DiagnosticRequest extends PairEvent
case class DiagnosticInfo(version: String) extends PairEvent

case class CreateDocument(path: String, content: Content) extends PairEvent
case class CreateDocumentConfirmation(path: String, version: Int, content: Content) extends PairEvent
case class ChangeContentEvent(eventId: String, path: String, baseVersion: Int, diffs: Seq[ContentDiff]) extends PairEvent
case class ChangeContentConfirmation(forEventId: String, path: String, newVersion: Int, diffs: Seq[ContentDiff]) extends PairEvent
case class CreateServerDocumentRequest(path: String) extends PairEvent
case class GetDocumentSnapshot(fromClientId: String, path: String) extends PairEvent
case class DocumentSnapshotEvent(path: String, version: Int, content: Content) extends PairEvent

case class MoveCaretEvent(path: String, offset: Int) extends PairEvent

case class CreateDirEvent(path: String) extends PairEvent
case class DeleteDirEvent(path: String) extends PairEvent
case class CreateFileEvent(path: String, content: Content) extends PairEvent
case class DeleteFileEvent(path: String) extends PairEvent
case class RenameDirEvent(path: String, newName: String) extends PairEvent
case class RenameFileEvent(path: String, newName: String) extends PairEvent
case class MoveDirEvent(path: String, newParentPath: String) extends PairEvent
case class MoveFileEvent(path: String, newParentPath: String) extends PairEvent

case class ProjectOperationFailed(message: String) extends PairEvent
case class CreatedProjectEvent(projectName: String, clientName: String) extends PairEvent
case class JoinedToProjectEvent(projectName: String, clientName: String) extends PairEvent
case class CreateProjectRequest(projectName: String, clientName: String) extends PairEvent
case class JoinProjectRequest(projectName: String, clientName: String) extends PairEvent
case class WatchFilesRequest(files: Seq[String]) extends PairEvent
case class WatchFilesChangedEvent(files: Seq[String]) extends PairEvent
case class ChangeMasterRequest(clientName: String) extends PairEvent

case class SelectContentEvent(path: String, offset: Int, length: Int) extends PairEvent

case class ServerStatusResponse(projects: Seq[ProjectInfoData], freeClients: Int) extends PairEvent {
  def findProject(name: String) = projects.find(_.name == name)
}

case class ProjectInfoData(name: String, clients: Seq[ClientInfoResponse], watchingFiles: Seq[String], workingMode: WorkingMode.Value) {
  def isCaretSharing = workingMode == WorkingMode.CaretSharing
}

case class ClientInfoResponse(clientId: String, project: String, name: String, isMaster: Boolean) extends PairEvent
case class ServerErrorResponse(message: String) extends PairEvent
case class ServerMessageResponse(message: String) extends PairEvent

case class OpenTabEvent(path: String) extends PairEvent
case class CloseTabEvent(path: String) extends PairEvent

case class ChangeModeEvent(message: String) extends PairEvent
case object AskForWorkingMode extends PairEvent

case object SyncFilesForAll extends PairEvent
case class SyncFilesRequest(fromClientId: String, fileSummaries: Seq[FileSummary]) extends PairEvent
case class SyncFileEvent(fromClientId: String, toClientId: String, path: String, content: Content) extends PairEvent
case class GetWatchingFilesFromPair(fromClientId: String, toClientId: String) extends PairEvent
case class WatchingFiles(fromClientId: String, toClientId: String, fileSummaries: Seq[FileSummary]) extends PairEvent
case class MasterWatchingFiles(fromClientId: String, toClientId: String, paths: Seq[String], diffFiles: Int) extends PairEvent
case class FileSummary(path: String, summary: String)

trait WorkingModeEvent extends PairEvent
case object CaretSharingModeRequest extends WorkingModeEvent
case object ParallelModeRequest extends WorkingModeEvent

case object ImMonitor extends PairEvent
case class MonitorEvent(projectName: String, realEventMessage: String, timestamp: Long = System.currentTimeMillis()) extends PairEvent
