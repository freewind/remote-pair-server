package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MySpecification
import com.thoughtworks.pli.intellij.remotepair.server.{Project, Projects}

class JoinProjectSpec extends MySpecification {

  "When server receives CreateProjectRequest, it" should {
    "create a new project and join the client with the provided client name" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      handler.projects must haveProjectMembers("test", Seq("Freewind"))
    }
    "response JoinedToProjectEvent if created successfully" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      there was one(context1).writeAndFlush(JoinedToProjectEvent("test", "Freewind").toMessage)
    }
    "move the client from old project to new if it has already in some project" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind"), CreateProjectRequest("test2", "Freewind"))
      handler.projects must haveProjectMembers("test2", Seq("Freewind"))
      handler.projects must haveProjectMembers("test1", Nil)
    }
    "response an error response if the name is already used" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test", "Freewind"), CreateProjectRequest("test", "Freewind"))
      there was one(context1).writeAndFlush(ProjectOperationFailed("Project 'test' is already existed").toMessage)
    }
  }

  "When server receives JoinProjectRequest, it" should {
    "join the client into to requested project" in new ProtocolMocking {
      client(context1, context2)
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      client(context2).send(JoinProjectRequest("test", "Lily"))
      handler.projects must haveProjectMembers("test", Seq("Freewind", "Lily"))
    }
    "response JoinedToProjectEvent if joined successfully" in new ProtocolMocking {
      client(context1, context2)
      client(context1).send(CreateProjectRequest("test", "Freewind"))
      client(context2).send(JoinProjectRequest("test", "Lily"))
      there was one(context2).writeAndFlush(JoinedToProjectEvent("test", "Lily").toMessage)
    }
    "response an error response if the requested project is not exist" in new ProtocolMocking {
      client(context1).send(JoinProjectRequest("non-exist", "Freewind"))
      there was one(context1).writeAndFlush(ProjectOperationFailed("Project 'non-exist' is not existed").toMessage)
    }
    "move the client from old project to the new one if the client is already in some project" in new ProtocolMocking {
      client(context1, context2)
      client(context1).send(CreateProjectRequest("test1", "Freewind"))
      client(context2).send(CreateProjectRequest("test2", "Lily"))
      client(context1).send(JoinProjectRequest("test2", "Freewind"))
      handler.projects must haveProjectMembers("test2", Seq("Lily", "Freewind"))
      handler.projects must haveProjectMembers("test1", Nil)
    }
    "response an error response if the client's name is already taken" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind"))
      client(context2).send(JoinProjectRequest("test1", "Freewind"))
      there was one(context2).writeAndFlush(ProjectOperationFailed("The client name 'Freewind' is already used in project 'test1'").toMessage)
      handler.projects.get("test1").map(_.members.size) must beSome(1)
    }
    "use the new client name if the client is already in the requested project" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind")).send(JoinProjectRequest("test1", "Lily"))
      handler.projects.get("test1").map(_.members.flatMap(_.name)) must beSome(Seq("Lily"))
    }
  }

  "Project on server" should {
    "be kept even if all its members are left" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind")).send(CreateProjectRequest("test2", "Freewind"))
      handler.projects.all.size === 2
      handler.projects must haveProjectMembers("test1", Seq())
      handler.projects must haveProjectMembers("test2", Seq("Freewind"))
    }
  }

  "User who has not joined to any project" should {
    "still able to receive ServerStatusResponse" in new ProtocolMocking {
      client(context1, context2)
      there was one(context1).writeAndFlush(ServerStatusResponse(
        Nil,
        freeClients = 2
      ).toMessage)
    }
    "still able to receive ServerErrorResponse" in new ProtocolMocking {
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
      cannotSendEvents(changeMasterEvent)
    }
    "not send IgnoreFilesRequest related request" in new ProtocolMocking {
      cannotSendEvents(IgnoreFilesRequest(Seq("/aaa")))
    }
    "not send SyncFilesRequest related request" in new ProtocolMocking {
      cannotSendEvents(syncFilesRequest)
    }

    def cannotSendEvents(events: PairEvent*) = new ProtocolMocking {
      client(context1).send(events: _*)

      events.map(event => there was no(context1).writeAndFlush(event.toMessage))
      there were atLeast(events.size)(context1).writeAndFlush(InvalidOperationState("You need to join a project first").toMessage)
    }
  }

  "Client who has joined to a project" should {
    "only receive events from users in the same project" in new ProtocolMocking {
      client(context1, context2, context3)

      client(context1).send(CreateProjectRequest("p1", "Freewind"))
      client(context2).send(JoinProjectRequest("p1", "Lily"))
      client(context3).send(CreateProjectRequest("p3", "Mike"))

      client(context1).send(openTabEvent1)

      there was one(context2).writeAndFlush(openTabEvent1.toMessage)
      there was no(context3).writeAndFlush(openTabEvent1.toMessage)
    }
  }

  "When a client joins a project, it" should {
    "send ClientInfoResponse to the client" in new ProtocolMocking {
      client(context1).createOrJoinProject("test1")
      there was one(context1).writeAndFlush(ClientInfoResponse(clientId(context1), "test1", "Freewind", isMaster = true).toMessage)
    }
    "not send it to other members of the same project" in new ProtocolMocking {
      client(context1).send(CreateProjectRequest("test1", "Freewind"))
      client(context2).send(JoinProjectRequest("test1", "Lily"))
      there was one(context2).writeAndFlush(ClientInfoResponse(clientId(context2), "test1", "Lily", isMaster = false).toMessage)
      there was no(context2).writeAndFlush(ClientInfoResponse(clientId(context1), "test1", "Freewind", isMaster = true).toMessage)
    }
  }

  "When a client changes the working mode, it" should {
    "send ClientInfoResponse to it as well" in new ProtocolMocking {
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
