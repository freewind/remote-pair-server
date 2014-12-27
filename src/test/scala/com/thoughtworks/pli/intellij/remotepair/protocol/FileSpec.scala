package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class FileSpec extends MySpecification {

  "When server receives file related events, it" should {
    def checking(event: PairEvent) = new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test")

      client(context1).send(event, event)

      there was two(context2).writeAndFlush(event.toMessage)
    }
    "broadcast to other contexts for CreateFileEvent" in new ProtocolMocking {
      checking(createFileEvent)
    }
    "broadcast to other contexts for DeleteFileEvent" in new ProtocolMocking {
      checking(deleteFileEvent)
    }
    "broadcast to other contexts for CreateDirEvent" in new ProtocolMocking {
      checking(createDirEvent)
    }
    "broadcast to other contexts for DeleteDirEvent" in new ProtocolMocking {
      checking(deleteDirEvent)
    }
    "broadcast to other contexts for RenameEvent" in new ProtocolMocking {
      checking(renameEvent)
    }
  }

}
