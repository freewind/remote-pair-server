package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.ServerLogger
import org.json4s.native.Serialization

class ParseEvent {

  def apply(line: String): PairEvent = {
    val (name, json) = line.span(_ != ' ')
    name match {
      case "OpenTabEvent" => Serialization.read[OpenTabEvent](json)
      case "CloseTabEvent" => Serialization.read[CloseTabEvent](json)
      case "ChangeContentEvent" => Serialization.read[ChangeContentEvent](json)
      case "ChangeMasterRequest" => Serialization.read[ChangeMasterRequest](json)
      case "ResetTabEvent" => Serialization.read[ResetTabEvent](json)
      case "CreateFileEvent" => Serialization.read[CreateFileEvent](json)
      case "DeleteFileEvent" => Serialization.read[DeleteFileEvent](json)
      case "CreateDirEvent" => Serialization.read[CreateDirEvent](json)
      case "DeleteDirEvent" => Serialization.read[DeleteDirEvent](json)
      case "RenameEvent" => Serialization.read[RenameEvent](json)
      case "MoveCaretEvent" => Serialization.read[MoveCaretEvent](json)
      case "SelectContentEvent" => Serialization.read[SelectContentEvent](json)
      case "PairableFilesRequest" => Serialization.read[PairableFilesRequest](json)
      case "SyncFilesRequest" => Serialization.read[SyncFilesRequest](json)
      case "SyncFilesForAll" => SyncFilesForAll
      case "CaretSharingModeRequest" => CaretSharingModeRequest
      case "CreateProjectRequest" => Serialization.read[CreateProjectRequest](json)
      case "JoinProjectRequest" => Serialization.read[JoinProjectRequest](json)
      case "JoinedToProjectEvent" => Serialization.read[JoinedToProjectEvent](json)
      case "ParallelModeRequest" => ParallelModeRequest
      case "InvalidOperationState" => Serialization.read[InvalidOperationState](json)
      case "ProjectOperationFailed" => Serialization.read[ProjectOperationFailed](json)
      case "ServerStatusResponse" => Serialization.read[ServerStatusResponse](json)
      case "ClientInfoResponse" => Serialization.read[ClientInfoResponse](json)
      case "ServerErrorResponse" => Serialization.read[ServerErrorResponse](json)
      case "ResetTabRequest" => ResetTabRequest
      case "SyncFileEvent" => Serialization.read[SyncFileEvent](json)
      case "MasterPairableFiles" => Serialization.read[MasterPairableFiles](json)
      case "CreateDocument" => Serialization.read[CreateDocument](json)
      case "CreateDocumentConfirmation" => Serialization.read[CreateDocumentConfirmation](json)
      case "CreateServerDocumentRequest" => Serialization.read[CreateServerDocumentRequest](json)
      case "ChangeContentConfirmation" => Serialization.read[ChangeContentConfirmation](json)
      case "PairableFiles" => Serialization.read[PairableFiles](json)
      case "GetPairableFilesFromPair" => Serialization.read[GetPairableFilesFromPair](json)
      case _ =>
        ServerLogger.info("!!!!!!!!!!!!!!!!!!!!! unknown line from server: " + line)
        ???
    }
  }

}
