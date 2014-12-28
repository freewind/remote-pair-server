package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification
import com.thoughtworks.pli.intellij.remotepair.server.{Project, Projects}
import com.thoughtworks.pli.intellij.remotepair.protocol._

import scalaz.Scalaz._

class JoinProjectSpec extends MySpecification {

  "AskForJoinProject" should {
    "send to client if has gotten client's information" in new ProtocolMocking {
      client(context1)
      there was one(context1).writeAndFlush(AskForJoinProject(None).toMessage)
    }
  }

  "CreateProjectRequest" should {
    "create a new project and join it with a new name on the server" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      handler.projects must haveProjectMembers("test", Seq("Freewind"))
    }
    "not create a project with existing name" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test", "Freewind"), CreateProjectRequest("test", "Freewind"))
      there was one(context1).writeAndFlush(AskForJoinProject(Some("Project 'test' is already existed")).toMessage)
    }
  }

  "JoinProjectRequest" should {
    "join an existing project" in new ProtocolMocking {
      client(context1, context2)
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      client(context2).send(JoinProjectRequest("test", "Lily"))
      handler.projects must haveProjectMembers("test", Seq("Freewind", "Lily"))
    }
    "not join an non-exist project" in new ProtocolMocking {
      client(context1).send(JoinProjectRequest("non-exist", "Freewind"))
      there was one(context1).writeAndFlush(AskForJoinProject(Some("Project 'non-exist' is not existed")).toMessage)
    }
    "leave original project when join another" in new ProtocolMocking {
      client(context1, context2)
      client(context1).send(CreateProjectRequest("test1", "Freewind"))
      client(context2).send(CreateProjectRequest("test2", "Lily"))
      client(context1).send(JoinProjectRequest("test2", "Freewind"))
      handler.projects must haveProjectMembers("test2", Seq("Lily", "Freewind"))
    }
    "not use a name which is already used in target project" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind"))
      client(context2).send(JoinProjectRequest("test1", "Freewind"))
      there was one(context2).writeAndFlush(AskForJoinProject(Some("The client name 'Freewind' is already used in project 'test1'")).toMessage)
      handler.projects.get("test1").map(_.members.size) must beSome(1)
    }
    "allow to join the project again" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind")).send(JoinProjectRequest("test1", "Lily"))
      handler.projects.get("test1").map(_.members.flatMap(_.name)) must beSome(Seq("Lily"))
    }
  }

  "Project on server" should {
    "be kept even if no one joined" in new ProtocolMocking {
      client(context1)
      client(context1).send(CreateProjectRequest("test1", "Freewind"), CreateProjectRequest("test2", "Freewind"))
      handler.projects.all.size === 2
      handler.projects must haveProjectMembers("test1", Seq())
      handler.projects must haveProjectMembers("test2", Seq("Freewind"))
    }
  }

  "User who has not joined to any project" should {
    "able to receive ServerStatusResponse" in new ProtocolMocking {
      client(context1, context2)
      there was one(context1).writeAndFlush(ServerStatusResponse(
        Nil,
        freeClients = 2
      ).toMessage)
    }
    "able to receive ServerErrorResponse" in new ProtocolMocking {
      client(context1).createOrJoinProject("test").changeMaster("non-exist-client")
      there was one(context1).writeAndFlush(ServerErrorResponse("Specified user 'non-exist-client' is not found").toMessage)
    }
    "not send editor related events" in new ProtocolMocking {
      cannotSendEvents(
        openTabEvent1, closeTabEvent, resetTabEvent,
        changeContentEventA1,
        moveCaretEvent,
        selectContentEvent
      )
    }
    "not send mode related request" in new ProtocolMocking {
      cannotSendEvents(
        CaretSharingModeRequest,
        ParallelModeRequest
      )
    }
    "not send master related request" in new ProtocolMocking {
      cannotSendEvents(changeContentEventA1)
    }
    "not send IgnoreFilesRequest related request" in new ProtocolMocking {
      cannotSendEvents(IgnoreFilesRequest(Seq("/aaa")))
    }
    "not send SyncFilesRequest related request" in new ProtocolMocking {
      cannotSendEvents(syncFilesRequest)
    }

    def cannotSendEvents(events: PairEvent*) = new ProtocolMocking {
      client(context1).send(events: _*)

      there were atLeast(events.size)(context1).writeAndFlush(AskForJoinProject(Some("You need to join a project first")).toMessage)
    }
  }

  "User who has joined to a project" should {
    "only receive events from users in the same project" in new ProtocolMocking {
      client(context1, context2, context3)

      client(context1).send(CreateProjectRequest("p1", "Freewind"))
      client(context2).send(JoinProjectRequest("p1", "Lily"))
      client(context3).send(CreateProjectRequest("p3", "Mike"))

      client(context1).send(changeContentEventA1)

      there was one(context2).writeAndFlush(changeContentEventA1.toMessage)
      there was no(context3).writeAndFlush(changeContentEventA1.toMessage)
    }
  }

  "ClientInfoResponse" should {
    "be sent to client when join a project" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      there was one(context1).writeAndFlush(ClientInfoResponse(clientId(context1), "test1", "Freewind", isMaster = true).toMessage)
    }
    "be sent to client when working mode changes" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      resetMocks(context1)
      client(context1).shareCaret()
      there was one(context1).writeAndFlush(ClientInfoResponse(clientId(context1), "test1", "Freewind", isMaster = true).toMessage)
    }
  }

  private def haveProjectMembers(projectName: String, members: Seq[String]) = {
    beSome[Project].which(_.members.flatMap(_.name) ==== members) ^^ { (x: Projects) => x.get(projectName)}
  }

}
