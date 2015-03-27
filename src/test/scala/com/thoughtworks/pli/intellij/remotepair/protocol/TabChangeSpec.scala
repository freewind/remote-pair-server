package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class TabChangeSpec extends MySpecification {

  "When a client sends an OpenTabEvent, the event" should {
    "be broadcast to all members of the same project" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()

      client(context1).send(openTabEvent1)

      there was one(context1).writeAndFlush(openTabEvent1.toMessage)
      there was one(context1).writeAndFlush(openTabEvent1.toMessage)
    }
  }

  "CloseTabEvent" should {
    "broadcast to others simply if in an caret-sharing project" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()

      client(context1).send(closeTabEvent)

      there was one(context2).writeAndFlush(closeTabEvent.toMessage)
    }
  }

}
