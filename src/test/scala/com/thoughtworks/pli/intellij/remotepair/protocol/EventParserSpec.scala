package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification
import com.thoughtworks.pli.intellij.remotepair.utils.{Delete, Insert}

class EventParserSpec extends MySpecification {

  val parser = new EventParser {}

  def parse(line: String, expectedEvent: PairEvent) = {
    val event = parser.parseEvent(line)
    event === expectedEvent
  }

  "EventParser" should {
    "parse AskForJoinProject" in {
      parse("AskForJoinProject {}", AskForJoinProject(None))
      parse( """AskForJoinProject {"message":"project not found"}""", AskForJoinProject(Some("project not found")))
    }
    "parse ServerErrorResponse" in {
      parse( """ServerErrorResponse {"message":"test-error"}""", ServerErrorResponse("test-error"))
    }
    "parse ServerStatusResponse" in {
      parse( """ServerStatusResponse {"projects":[{"name":"myname","clients":[{"clientId":"123","project":"test1","name":"user222","isMaster":true}],"ignoredFiles":[],"workingMode":"CaretSharing"}],"freeClients":0}""",
        ServerStatusResponse(Seq(ProjectInfoData("myname", Seq(ClientInfoResponse("123", "test1", "user222", isMaster = true)), Nil, WorkingMode.CaretSharing)), freeClients = 0))
    }
    "parse SyncFileEvent" in {
      parse( """SyncFileEvent {"fromClientId":"from-id","toClientId":"to-id","path":"/aaa","content":{"text":"my-content","charset":"UTF-8"}}""", SyncFileEvent("from-id", "to-id", "/aaa", Content("my-content", "UTF-8")))
    }
    "parse MasterPairableFiles" in {
      parse( """MasterPairableFiles {"fromClientId":"from-id","toClientId":"to-id","paths":["/aaa"]}""", MasterPairableFiles("from-id", "to-id", Seq("/aaa")))
    }
    "parse SyncFilesRequest" in {
      parse( """SyncFilesRequest {"fromClientId":"id1","fileSummaries":[{"path":"/aaa","summary":"s1"}]}""", SyncFilesRequest("id1", Seq(FileSummary("/aaa", "s1"))))
    }
    "parse CreateDocument" in {
      parse( """CreateDocument {"path":"/aaa","content":{"text":"my-content","charset":"UTF-8"}}""", CreateDocument("/aaa", Content("my-content", "UTF-8")))
    }
    "parse CreateDocumentConfirmation" in {
      parse( """CreateDocumentConfirmation {"path":"/aaa","version":12,"content":{"text":"my-content","charset":"UTF-8"}}""", CreateDocumentConfirmation("/aaa", 12, Content("my-content", "UTF-8")))
    }
    "parse CreateServerDocumentRequest" in {
      parse( """CreateServerDocumentRequest {"path":"/aaa"}""", CreateServerDocumentRequest("/aaa"))
    }
    "parse ChangeContentConfirmation" in {
      parse( """ChangeContentConfirmation {"forEventId":"uuid1","path":"/aaa","newVersion":3,"diffs":[{"op":"insert","offset":11,"content":"aa"},{"op":"delete","offset":43,"length":3}]}""", ChangeContentConfirmation("uuid1", "/aaa", 3, Seq(Insert(11, "aa"), Delete(43, 3))))
    }
    "parse ChangeContentEvent" in {
      parse( """ChangeContentEvent {"eventId":"myEventId","path":"/aaa","baseVersion":20,"diffs":[{"op":"insert","offset":10,"content":"abc"},{"op":"delete","offset":10,"length":2}]}""", ChangeContentEvent("myEventId", "/aaa", 20, Seq(Insert(10, "abc"), Delete(10, 2))))
    }
    "parse JoinedToProjectEvent" in {
      parse( """JoinedToProjectEvent {"projectName":"my-project","clientName":"my-name"}""", JoinedToProjectEvent("my-project", "my-name"))
    }
    "parse MoveCaretEvent" in {
      parse( """MoveCaretEvent {"path":"/aaa","offset":12}""", MoveCaretEvent("/aaa", 12))
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
    "parse RenameEvent" in {
      parse( """RenameEvent {"from":"/aaa","to":"/bbb"}""", RenameEvent("/aaa", "/bbb"))
    }
    "parse SyncFilesForAll" in {
      parse( """SyncFilesForAll {}""", SyncFilesForAll)
    }
    "parse ChangeMasterRequest" in {
      parse( """ChangeMasterRequest {"clientName":"myname"}""", ChangeMasterRequest("myname"))
    }
    "parse GetPairableFilesFromPair" in {
      parse( """GetPairableFilesFromPair {"fromClientId":"from-id","toClientId":"to-id"} """, GetPairableFilesFromPair("from-id", "to-id"))
    }
    "parse PairableFiles" in {
      parse( """PairableFiles {"fromClientId":"from-id","toClientId":"to-id","fileSummaries":[{"path":"/aaa","summary":"md5"}]}""", PairableFiles("from-id", "to-id", Seq(FileSummary("/aaa", "md5"))))
    }
  }
}
