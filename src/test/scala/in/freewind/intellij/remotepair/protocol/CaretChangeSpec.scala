package in.freewind.intellij.remotepair.protocol

import in.freewind.intellij.remotepair.MySpecification

class CaretChangeSpec extends MySpecification {

  "MoveCaretEvent" should {
    "be broadcast to other members if the project is in caret-sharing mode" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()

      client(context1).send(moveCaretEvent)

      there was one(context2).writeAndFlush(moveCaretEvent.toMessage)
    }
  }

}
