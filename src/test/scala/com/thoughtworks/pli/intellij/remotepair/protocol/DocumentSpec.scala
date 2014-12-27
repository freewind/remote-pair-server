package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair._
import com.thoughtworks.pli.intellij.remotepair.utils.Insert

class DocumentSpec extends MySpecification {

  "when server receives CreateDocument event from client, it" should {
    "response version 0 and init content if the its just be created" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      there was one(context1).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
    }
    "broadcast to all clients if the event is accepted" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
      there was one(context3).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
    }
    "response existing version and content to client again if the doc is already exist" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      resetMocks(context2)

      client(context2).send(CreateDocument("/aaa", Content("abc123", "UTF-8")))
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
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
      there was one(context1).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/bbb", 0, Content("abc", "GBK")).toMessage)
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
      there was one(context2).writeAndFlush(CreateDocumentConfirmation("/aaa", 0, Content("abc", "UTF-8")).toMessage)
    }
  }

  "ChangeContentEvent" should {
    "get confirmation with new version and changes based on old version if no conflict" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context1).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "123"))))
      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId1", "/aaa", 1, Seq(Insert(3, "123"))).toMessage)
    }
    "get confirmation with new version and changes based on old version if there is conflict" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test1")
      client(context1).send(CreateDocument("/aaa", Content("abc", "UTF-8")))
      client(context2).send(ChangeContentEvent("eventId1", "/aaa", 0, Seq(Insert(3, "111"))))
      client(context1).send(ChangeContentEvent("eventId2", "/aaa", 0, Seq(Insert(3, "222"))))

      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId1", "/aaa", 1, Seq(Insert(3, "111"))).toMessage)
      there was one(context1).writeAndFlush(ChangeContentConfirmation("eventId2", "/aaa", 2, Seq(Insert(6, "222"))).toMessage)
    }
  }

}
