package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class FileChangeSpec extends MySpecification {

  "SyncFilesRequest" should {
    "forward to master" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test")
      client(context2).beMaster()
      client(context1).send(syncFilesRequest)
      there was one(context2).writeAndFlush(syncFilesRequest.toMessage)
      there was no(context1).writeAndFlush(syncFilesRequest.toMessage)
    }
  }


}
