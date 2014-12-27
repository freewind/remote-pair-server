package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification

class WorkingModeSpec extends MySpecification {

  "CaretSharingMode" should {
    "mark the project in CaretSharingMode" in new ProtocolMocking {
      client(context1, context2, context3).createOrJoinProject("test").shareCaret()

      project("test").isSharingCaret === true
    }
    "be required from any client of a project" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").parallel()

      client(context1).shareCaret()

      project("test").isSharingCaret === true
    }
    "allow to broadcast many events with each other" should {
      def broadcast(events: PairEvent*) = new ProtocolMocking {
        client(context1, context2).createOrJoinProject("test").shareCaret()

        client(context1).send(events: _*)

        events.foreach { event =>
          there was one(context2).writeAndFlush(event.toMessage)
          there was no(context1).writeAndFlush(event.toMessage)
        }
      }
      "include tab events" in new ProtocolMocking {
        broadcast(openTabEvent1, closeTabEvent, resetTabEvent)
      }
      "include caret events" in new ProtocolMocking {
        broadcast(moveCaretEvent)
      }
      "include selection events" in new ProtocolMocking {
        broadcast(selectContentEvent1)
      }
    }

    "can't be issues if the client is not in any project" in new ProtocolMocking {
      client(context1).shareCaret()

      there was one(context1).writeAndFlush(AskForJoinProject(Some("You need to join a project first")).toMessage)
    }
  }

  "ParallelModeRequest" should {
    "be required by any client of a project" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").shareCaret()
      client(context1).parallel()
      project("test").isSharingCaret === false
    }
    "not broadcast tab events to other members" in new ProtocolMocking {
      client(context1, context2).createOrJoinProject("test").parallel()
      client(context1).send(openTabEvent1, closeTabEvent, resetTabEvent)

      there was no(context2).writeAndFlush(openTabEvent1.toMessage)
      there was no(context2).writeAndFlush(closeTabEvent.toMessage)
      there was no(context2).writeAndFlush(resetTabEvent.toMessage)
    }
  }

}
