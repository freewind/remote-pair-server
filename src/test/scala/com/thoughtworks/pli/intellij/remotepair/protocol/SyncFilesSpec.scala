package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class SyncFilesSpec extends MySpecification {

  "SyncFilesRequest" should {
    "be forwarded to master" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test")
      client(context2).beMaster()
      client(context1).send(syncFilesRequest)
      there was one(context2).writeAndFlush(syncFilesRequest.toMessage)
      there was no(context1).writeAndFlush(syncFilesRequest.toMessage)
    }
  }

  "If server receives some event with 'toClientId', it should only forward it to correct client" should {
    def check(x: String => PairEvent) = new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test")
      val event = x(clientId(context2))
      client(context1).send(event)
      there was one(context2).writeAndFlush(event.toMessage)
      there was no(context3).writeAndFlush(event.toMessage)
    }
    "for MasterPairableFiles" in check(new MasterPairableFiles(_, Nil))
    "for SyncFileEvent" in check(new SyncFileEvent(_, "/aaa", Content("abc", "UTF-8")))
  }

  "If server receives SyncFilesForAll, it" should {
    "broadcast it to all non-master clients" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test")
      // context2 is not master, context1 is
      client(context2).send(SyncFilesForAll)

      there was no(context1).writeAndFlush(SyncFilesForAll.toMessage)
      there was one(context2).writeAndFlush(SyncFilesForAll.toMessage)
      there was one(context3).writeAndFlush(SyncFilesForAll.toMessage)
    }
  }

}
