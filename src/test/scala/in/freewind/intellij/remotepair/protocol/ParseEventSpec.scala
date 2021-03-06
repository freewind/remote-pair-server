package in.freewind.intellij.remotepair.protocol

import in.freewind.intellij.remotepair.MySpecification
import in.freewind.intellij.remotepair.server.ClientIdName
import in.freewind.intellij.remotepair.utils.{Delete, Insert}

class ParseEventSpec extends MySpecification {

  val parseEvent = new ParseEvent {}

  def parse(line: String, expectedEvent: PairEvent) = {
    val event = parseEvent(line)
    event === expectedEvent
  }

  "ParseEvent" should {
    "parse InvalidOperationState" in {
      parse( """InvalidOperationState {"message":"you should join a project first"}""", InvalidOperationState("you should join a project first"))
    }
    "parse ProjectOperationFailed" in {
      parse( """ProjectOperationFailed {"message":"project not found"}""", ProjectOperationFailed("project not found"))
    }
    "parse ServerErrorResponse" in {
      parse( """ServerErrorResponse {"message":"test-error"}""", ServerErrorResponse("test-error"))
    }
    "parse ServerStatusResponse" in {
      parse(
        """ServerStatusResponse {"projects":[{"name":"myname","clients":[{"clientId":"123","project":"test1","name":"user222","isMaster":true}],"watchingFiles":["/aaa"],"workingMode":"CaretSharing"}],"freeClients":0}""",
        ServerStatusResponse(Seq(ProjectInfoData("myname", Seq(ClientInfoResponse("123", "test1", "user222", isMaster = true)), Seq("/aaa"), WorkingMode.CaretSharing)), freeClients = 0))
    }
    "parse SyncFileEvent" in {
      parse( """SyncFileEvent {"fromClientId":"from-id","toClientId":"to-id","path":"/aaa","content":{"text":"my-content","charset":"UTF-8"}}""", SyncFileEvent("from-id", "to-id", "/aaa", Content("my-content", "UTF-8")))
    }
    "parse MasterWatchingFiles" in {
      parse( """MasterWatchingFiles {"fromClientId":"from-id","toClientId":"to-id","paths":["/aaa"],"diffFiles":2}""", MasterWatchingFiles("from-id", "to-id", Seq("/aaa"), 2))
    }
    "parse SyncFilesRequest" in {
      parse( """SyncFilesRequest {"fromClientId":"id1","fileSummaries":[{"path":"/aaa","summary":"s1"}]}""", SyncFilesRequest("id1", Seq(FileSummary("/aaa", "s1"))))
    }
    "parse CreateDocument" in {
      parse( """CreateDocument {"path":"/aaa","content":{"text":"my-content","charset":"UTF-8"}}""", CreateDocument("/aaa", Content("my-content", "UTF-8")))
    }
    "parse CreateDocumentConfirmation" in {
      parse( """CreateDocumentConfirmation {"path":"/aaa","version":12,"content":{"text":"my-content","charset":"UTF-8"},"sourceClient":{"id":"id123456","name":"freewind"}}""", CreateDocumentConfirmation("/aaa", 12, Content("my-content", "UTF-8"), ClientIdName("id123456","freewind")))
    }
    "parse CreateServerDocumentRequest" in {
      parse( """CreateServerDocumentRequest {"path":"/aaa"}""", CreateServerDocumentRequest("/aaa"))
    }
    "parse ChangeContentConfirmation" in {
      parse( """ChangeContentConfirmation {"forEventId":"uuid1","path":"/aaa","newVersion":3,"diffs":[{"op":"insert","offset":11,"content":"aa"},{"op":"delete","offset":43,"length":3}],"sourceClient":{"id":"freewind","name":"id123456"}}""", ChangeContentConfirmation("uuid1", "/aaa", 3, Seq(Insert(11, "aa"), Delete(43, 3)), ClientIdName("freewind", "id123456")))
    }
    "parse ChangeContentEvent" in {
      parse( """ChangeContentEvent {"eventId":"myEventId","path":"/aaa","baseVersion":20,"diffs":[{"op":"insert","offset":10,"content":"abc"},{"op":"delete","offset":10,"length":2}]}""", ChangeContentEvent("myEventId", "/aaa", 20, Seq(Insert(10, "abc"), Delete(10, 2))))
    }
    "parse JoinedToProjectEvent" in {
      parse( """JoinedToProjectEvent {"projectName":"my-project","clientName":"my-name"}""", JoinedToProjectEvent("my-project", "my-name"))
    }
    "parse MoveCaretEvent" in {
      parse( """MoveCaretEvent {"path":"/aaa","offset":12,"sourceClient":{"id":"id123456","name":"freewind"}}""", MoveCaretEvent("/aaa", 12, ClientIdName("id123456", "freewind")))
    }
    "parse CreateDirEvent" in {
      parse( """CreateDirEvent {"path":"/aaa"} """, CreateDirEvent("/aaa"))
    }
    "parse CreateFileEvent" in {
      parse( """CreateFileEvent {"path":"/aaa","content":{"text":"full-content","charset":"UTF-8"}}""", CreateFileEvent("/aaa", Content("full-content", "UTF-8")))
    }
    "parse DeleteDirEvent" in {
      parse( """DeleteDirEvent {"path":"/aaa"}""", DeleteDirEvent("/aaa"))
    }
    "parse DeleteFileEvent" in {
      parse( """DeleteFileEvent {"path":"/aaa"}""", DeleteFileEvent("/aaa"))
    }
    "parse RenameDirEvent" in {
      parse( """RenameDirEvent {"path":"/ddd/aaa","newName":"bbb"}""", RenameDirEvent("/ddd/aaa", "bbb"))
    }
    "parse RenameFileEvent" in {
      parse( """RenameFileEvent {"path":"/ddd/aaa","newName":"bbb"}""", RenameFileEvent("/ddd/aaa", "bbb"))
    }
    "parse MoveDirEvent" in {
      parse( """MoveDirEvent {"path":"/ddd/aaa","newParentPath":"/ccc"}""", MoveDirEvent("/ddd/aaa", "/ccc"))
    }
    "parse MoveFileEvent" in {
      parse( """MoveFileEvent {"path":"/ddd/aaa","newParentPath":"/ccc"}""", MoveFileEvent("/ddd/aaa", "/ccc"))
    }
    "parse SyncFilesForAll" in {
      parse( """SyncFilesForAll {}""", SyncFilesForAll)
    }
    "parse ChangeMasterRequest" in {
      parse( """ChangeMasterRequest {"clientName":"myname"}""", ChangeMasterRequest("myname"))
    }
    "parse GetWatchingFilesFromPair" in {
      parse( """GetWatchingFilesFromPair {"fromClientId":"from-id","toClientId":"to-id"} """, GetWatchingFilesFromPair("from-id", "to-id"))
    }
    "parse WatchingFiles" in {
      parse( """WatchingFiles {"fromClientId":"from-id","toClientId":"to-id","fileSummaries":[{"path":"/aaa","summary":"md5"}]}""", WatchingFiles("from-id", "to-id", Seq(FileSummary("/aaa", "md5"))))
    }
    "parse CreatedProjectEvent" in {
      parse( """CreatedProjectEvent {"projectName":"project-name","clientName":"client-name"}""", CreatedProjectEvent("project-name", "client-name"))
    }
    "parse DocumentSnapshotEvent" in {
      parse( """DocumentSnapshotEvent {"path":"/abc","version":3,"content":{"text":"hello","charset":"UTF-8"}}""", DocumentSnapshotEvent("/abc", 3, Content("hello", "UTF-8")))
    }
    "parse GetDocumentSnapshot" in {
      parse( """GetDocumentSnapshot {"fromClientId":"from-client-id","path":"/abc"}""", GetDocumentSnapshot("from-client-id", "/abc"))
    }
    "parse DiagnosticInfo" in {
      parse( """DiagnosticInfo {"version":"1.2.3"}""", DiagnosticInfo("1.2.3"))
    }
    "parse DiagnosticRequest" in {
      parse( """DiagnosticRequest {}""", DiagnosticRequest)
    }
    "parse WatchFilesChangedEvent" in {
      parse( """WatchFilesChangedEvent {"files":["/aaa","/bbb"]}""", WatchFilesChangedEvent(Seq("/aaa", "/bbb")))
    }
    "parse ServerVersionInfo" in {
      parse( """ServerVersionInfo { "version": "1.2.3" }""", ServerVersionInfo("1.2.3"))
    }
  }
}
