package in.freewind.intellij.remotepair.protocol

import in.freewind.intellij.remotepair.MySpecification

class SelectionSpec extends MySpecification {

  "SelectContentEvent" should {
    "be broadcasted to other clients simply" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test").shareCaret()
      client(context1).send(selectContentEvent)
      there was one(context2).writeAndFlush(selectContentEvent.toMessage)
      there was one(context3).writeAndFlush(selectContentEvent.toMessage)
    }
  }

}
