package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class TabChangeSpec extends MySpecification {

  "When a client receives an OpenTabEvent, it" should {
    "issue the same event before issue OpenTabEvent with other path" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()

      client(context1).send(openTabEvent1)
      client(context2).send(openTabEvent1)
      client(context2).send(openTabEvent2)

      there was no(context1).writeAndFlush(openTabEvent1.toMessage)
      there was one(context1).writeAndFlush(openTabEvent2.toMessage)
    }
  }

  "If a client sends an OpenTabEvent with different path when it receives an OpenTabEvent, the other client" should {
    "not receive it" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()

      client(context1).send(openTabEvent1)
      client(context2).send(openTabEvent2)

      there was no(context1).writeAndFlush(openTabEvent2.toMessage)
    }
    "receives a ResetTabRequest if it's master" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test").shareCaret()

      client(context3).send(openTabEvent1)
      client(context2).send(openTabEvent2)

      there was one(context1).writeAndFlush(resetTabRequest.toMessage)
      there was no(context3).writeAndFlush(resetTabRequest.toMessage)
    }
  }

  "When client receives an TabResetEvent, it" should {
    "just need to send an OpenTabEvent with same path before send OpenTabEvent with other paths" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()
      client(context1).send(openTabEvent1, openTabEvent2, resetTabEvent)
      client(context2).send(openTabEvent3, openTabEvent2)
      there was one(context1).writeAndFlush(openTabEvent2.toMessage)
      there was no(context1).writeAndFlush(openTabEvent3.toMessage)
    }
    "be able to send OpenTabEvent with other paths immediately if it's master" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()
      client(context2).send(openTabEvent1)
      client(context1).send(resetTabEvent, openTabEvent2)
      there was one(context2).writeAndFlush(openTabEvent2.toMessage)
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
