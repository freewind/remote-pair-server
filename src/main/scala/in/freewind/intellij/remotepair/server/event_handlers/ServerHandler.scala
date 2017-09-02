package in.freewind.intellij.remotepair.server.event_handlers

import in.freewind.intellij.remotepair._
import in.freewind.intellij.remotepair.protocol._
import in.freewind.intellij.remotepair.server.{Client, Clients, Projects}
import in.freewind.intellij.remotepair.utils.IsSubPath
import io.netty.channel._

object ServerHandler {
  type Factory = () => ServerHandler
}

class ServerHandler(clients: Clients, projects: Projects, parseEvent: ParseEvent, isSubPath: IsSubPath,
                    handleEventInProject: HandleEventInProject,
                    broadcast: Broadcast,
                    sendToMaster: SendToMaster,
                    handleCreateProjectRequest: HandleCreateProjectRequest,
                    handleJoinProjectRequest: HandleJoinProjectRequest,
                    handleDiagnosticRequest: HandleDiagnosticRequest,
                    handleImMonitor: HandleImMonitor) extends ChannelHandlerAdapter {

  override def channelActive(ctx: ChannelHandlerContext) {
    val client = clients.newClient(ctx)
    client.writeEvent(ServerVersionInfo(BuildInfo.version))
    broadcast.serverStatusResponse(Some(client))
  }

  override def channelInactive(ctx: ChannelHandlerContext) {
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

    removeFromProject(clients.get(ctx).get)
    clients.removeClient(ctx)
    broadcast.serverStatusResponse(None)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    cause.printStackTrace()
    clients.get(ctx).foreach(_.writeEvent(ServerErrorResponse(cause.toString)))
  }

  override def channelRead(context: ChannelHandlerContext, msg: Any) = msg match {
    case line: String => val client = clients.get(context).get

      ServerLogger.info(client.name + " -> server: " + line)
      projects.findForClient(client) match {
        case Some(project) => handleEventInProject(project, parseEvent(line), client)
        case _ => handleEventWithoutProject(parseEvent(line), client)
      }
    case _ => throw new Exception("### unknown msg type: " + msg)
  }


  private def handleEventWithoutProject(event: PairEvent, client: Client) = {
    event match {
      case CreateProjectRequest(projectName, clientName) => handleCreateProjectRequest(client, projectName, clientName)
      case JoinProjectRequest(projectName, clientName) => handleJoinProjectRequest(client, projectName, clientName)
      case DiagnosticRequest => handleDiagnosticRequest(client)
      case ImMonitor => handleImMonitor(client)
      case _ => client.writeEvent(InvalidOperationState("You need to join a project first"))
    }
  }

}

