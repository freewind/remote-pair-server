package in.freewind.intellij.remotepair.protocol

import in.freewind.intellij.remotepair._
import in.freewind.intellij.remotepair.utils.Insert

class DocumentSpec extends MySpecification {

  "when server receives CreateDocument event from client, it" should {
    "response version 0 and init content if its just be created" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      there was one(context1).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
    }
    "broadcast to all clients if the event is accepted" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
      there was one(context3).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
    }
    "response existing version and content to client again if the doc is already exist" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      resetMocks(context2)

      client(context2).send(CreateDocument("/aaa", Content("abc123", "UTF-8")))
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
      // FIXME !!!! contain is not treated as a matcher here
      there was no(context2).writeAndFlush(contain("abc123"))
    }
    "not broadcast to others if the event is not accepted" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      resetMocks(context1)

      client(context2).send(CreateDocument("/aaa", Content("this-is-not-accepted", "UTF-8")))
      there was no(context1).writeAndFlush(any)
    }
    "allow to create document for different paths" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context2).send(CreateDocument("/bbb", Content("abc", "GBK")))
      there was one(context1).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/bbb", 0, Content("abc", "GBK"), clientIdName(context2)).toMessage)
    }
  }

  "When server receives CreateServerDocumentRequest, it" should {
    "forward it to the master if there is no doc in server" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context2).send(CreateServerDocumentRequest("/aaa"))
      there was one(context1).writeAndFlush(CreateServerDocumentRequest("/aaa").toMessage)
    }
    "forward it to the master if there is no doc in server and even if the client is master" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateServerDocumentRequest("/aaa"))
      there was one(context1).writeAndFlush(CreateServerDocumentRequest("/aaa").toMessage)
    }
    "response CreateDocumentConfirmation with existing version and content to client only if the doc already existed in server" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      resetMocks(context1, context2)

      client(context2).send(CreateServerDocumentRequest("/aaa"))
      there was no(context1).writeAndFlush(any)
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8"), clientIdName(context1)).toMessage)
    }
  }

  "ChangeContentEvent" should {
    "get confirmation with new version and changes based on old version if no conflict" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "123"))))
      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId1", "/aaa", 1, Seq(Insert(3, "123")), clientIdName(context1)).toMessage)
    }
    "get confirmation with new version and changes based on old version if there is conflict" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context2).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "111"))))
      client(context1).send(ChangeContentEvent("eventId2", "/aaa", 0, Seq(Insert(3, "222"))))

      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId1", "/aaa", 1, Seq(Insert(3, "111")), clientIdName(context2)).toMessage)
      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId2", "/aaa", 2, Seq(Insert(6, "222")), clientIdName(context1)).toMessage)
    }
    "not change doc version on server if the diffs is empty" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Nil))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "111"))))
      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId1", "/aaa", 1, Seq(Insert(3, "111")), clientIdName(context1)).toMessage)
    }
  }

  "When server receives an ChangeContentEvent, but there is no corresponding doc, it" should {
    "response an error" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "111"))))
      there was one(context1).writeAndFlush(ServerErrorResponse("The document of '/aaa' is not existed on server").toMessage)
    }
  }

  "In order to improve performance, the server" should {
    "track the versions of a document the clients hold and calculate latest possible content ASAP for the case of 1 client" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))

      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(0, "x"))))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 1, Seq(Insert(0, "x"))))

      project("test1").documents.find("/aaa").map(_.initContent.text) ==== Some("xabc")
      project("test1").documents.find("/aaa").map(_.initVersion) ==== Some(1)
      project("test1").documents.find("/aaa").map(_.versions.head.version) ==== Some(2)
    }

    "track the versions of a document the clients hold and calculate latest content ASAP for the case of multi clients" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))

      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(0, "x"))))
      client(context2).send(ChangeContentEvent("eventId1", "/aaa", 0, Nil))

      1 to 10 foreach { version =>
        client(context1).send(ChangeContentEvent("eventId1", "/aaa", version, Seq(Insert(0, "x"))))
      }

      client(context2).send(ChangeContentEvent("eventId1", "/aaa", 4, Seq(Insert(0, "b"))))

      project("test1").documents.find("/aaa").map(_.initContent.text) ==== Some("xxxxabc")
      project("test1").documents.find("/aaa").map(_.initVersion) ==== Some(4)
      project("test1").documents.find("/aaa").map(_.versions.head.version) ==== Some(5)
    }

    "track the correct version even if a client is disconnected" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))

      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(0, "x"))))
      client(context2).send(ChangeContentEvent("eventId1", "/aaa", 0, Nil))
      client(context2).disconnect()

      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 1, Seq(Insert(0, "x"))))

      project("test1").documents.find("/aaa").map(_.initContent.text) ==== Some("xabc")
      project("test1").documents.find("/aaa").map(_.initVersion) ==== Some(1)
      project("test1").documents.find("/aaa").map(_.versions.head.version) ==== Some(2)
    }

  }

  "When all clients of a project left, the server" should {
    "clear all held documents" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context2).send(CreateDocument("/bbb", Content("abc", "UTF-8")))
      client(context1, context2).disconnect()

      project("test1").documents.allPaths === Nil
    }
  }

  "When server receives GetDocumentSnapshot from a client, it" should {
    "response DocumentSnapshotEvent to the client" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "123"))))
      client(context1).send(ChangeContentEvent("eventId2", "/aaa", 1, Seq(Insert(3, "xyz"))))
      client(context1).send(GetDocumentSnapshot(clientId(context1), "/aaa"))
      there was one(context1).writeAndFlush(DocumentSnapshotEvent("/aaa", 2, Content("abcxyz123", "UTF-8")).toMessage)
    }
    "only response DocumentSnapshotEvent to requesting client" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      resetMocks(context1)
      client(context2).send(GetDocumentSnapshot(clientId(context2), "/aaa"))
      // FIXME find a better way
      // there was one(context2).writeAndFlush(contain("DocumentSnapshotEvent"))
      there was no(context1).writeAndFlush(any)
    }
  }

}
