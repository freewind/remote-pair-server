package com.thoughtworks.pli.intellij.remotepair.server

import akka.actor.{ActorRef, Actor}
import akka.remote.{DisassociatedEvent, AssociatedEvent, RemotingLifecycleEvent}
import com.thoughtworks.pli.intellij.remotepair.ServerLogger
import com.thoughtworks.pli.intellij.remotepair.protocol._
import com.thoughtworks.pli.intellij.remotepair.server.event_handlers._
import com.thoughtworks.pli.intellij.remotepair.utils.IsSubPath

class ServerActor(clients: Clients, projects: Projects, parseEvent: ParseEvent, isSubPath: IsSubPath,
                  handleEventInProject: HandleEventInProject,
                  broadcastServerStatusResponse: BroadcastServerStatusResponse,
                  broadcastToSameProjectMembersThen: BroadcastToSameProjectMembersThen,
                  sendToMaster: SendToMaster,
                  handleCreateProjectRequest: HandleCreateProjectRequest,
                  handleJoinProjectRequest: HandleJoinProjectRequest,
                  handleDiagnosticRequest: HandleDiagnosticRequest,
                  broadcastToOtherMembers: BroadcastToOtherMembers) extends Actor {

  context.system.eventStream.subscribe(self, classOf[RemotingLifecycleEvent])


  override def receive: Receive = {
    case AssociatedEvent(_, _, _) => createClientAndBroadcast(sender())
    case DisassociatedEvent(_, _, _) => removeClientFromProject(sender())
    case event: PairEvent =>
      clients.get(sender()) match {
        case Some(client) =>
          ServerLogger.info(client.name + " -> server: " + event)
          projects.findForClient(client) match {
            case Some(project) => handleEventInProject(project, event, client)
            case _ => handleEventWithoutProject(event, client)
          }
        case _ => ???
      }
  }

  private def createClientAndBroadcast(sender1: ActorRef): Unit = {
    broadcastServerStatusResponse(Some(clients.newClient(sender1)))
  }

  private def removeClientFromProject(sender1: ActorRef): Unit = {
    def removeFromProject(client: Client): Unit = {
      projects.findForClient(client).foreach { project =>
        project.removeMember(client)
        if (client.isMaster) {
          project.members.headOption.foreach(_.isMaster = true)
        }
        if (project.members.isEmpty) {
          project.documents.clearAll()
        }
      }
    }

    removeFromProject(clients.get(sender1).get)
    clients.removeClient(sender1)
    broadcastServerStatusResponse(None)
  }

  private def handleEventWithoutProject(event: PairEvent, client: Client) = event match {
    case CreateProjectRequest(projectName, clientName) => handleCreateProjectRequest(client, projectName, clientName)
    case JoinProjectRequest(projectName, clientName) => handleJoinProjectRequest(client, projectName, clientName)
    case DiagnosticRequest => handleDiagnosticRequest(client)
    case _ => client.writeEvent(InvalidOperationState("You need to join a project first"))
  }

}

class ClientNotFoundException(sender: ActorRef) extends Exception
