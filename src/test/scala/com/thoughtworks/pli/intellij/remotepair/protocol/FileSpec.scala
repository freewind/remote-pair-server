package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class FileSpec extends MySpecification {

  "When server receives file related events, it" should {
    def broadcast(event: PairEvent) = new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test")

      client(context1).send(event, event)

      there was two(context2).writeAndFlush(event.toMessage)
    }
    "broadcast to other contexts for CreateFileEvent" in new ProtocolMocking {
      broadcast(createFileEvent)
    }
    "broadcast to other contexts for DeleteFileEvent" in new ProtocolMocking {
      broadcast(deleteFileEvent)
    }
    "broadcast to other contexts for CreateDirEvent" in new ProtocolMocking {
      broadcast(createDirEvent)
    }
    "broadcast to other contexts for DeleteDirEvent" in new ProtocolMocking {
      broadcast(deleteDirEvent)
    }
    "broadcast to other contexts for RenameEvent" in new ProtocolMocking {
      broadcast(renameEvent)
    }
  }

  "If server receives an DeleteFileEvent, it" should {
    "delete corresponding server document if there exists" in new ProtocolMocking {
      client(context1).createOrJoinProject("test")
      client(context1).send(CreateDocument("/aaa", Content("abc123", "UTF-8")))
      client(context1).send(DeleteFileEvent("/aaa"))
      project("test").documents.find("/aaa") ==== None
    }
  }

  "If server receives an DeleteDirEvent, it" should {
    "delete all server documents under this dir if existed" in new ProtocolMocking {
      client(context1).createOrJoinProject("test")
      client(context1).send(CreateDocument("/aaa/11", Content("abc123", "UTF-8")))
      client(context1).send(CreateDocument("/aaa/22/33", Content("abc123", "UTF-8")))
      client(context1).send(CreateDocument("/bbb", Content("abc123", "UTF-8")))
      client(context1).send(DeleteDirEvent("/aaa"))
      project("test").documents.find("/aaa/11") ==== None
      project("test").documents.find("/aaa/22/33") ==== None
      project("test").documents.find("/bbb") must beSome
    }
  }

}
