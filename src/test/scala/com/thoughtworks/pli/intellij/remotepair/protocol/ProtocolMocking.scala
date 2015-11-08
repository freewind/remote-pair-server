package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.MyMocking
import com.thoughtworks.pli.intellij.remotepair.server.ClientIdName
import com.thoughtworks.pli.intellij.remotepair.server.event_handlers.ServerHandlerModule
import com.thoughtworks.pli.intellij.remotepair.utils.Insert
import io.netty.channel.ChannelHandlerContext

trait ProtocolMocking extends MyMocking with MockEvents with ServerHandlerModule {
  m =>

  def dataOf(context: ChannelHandlerContext) = {
    clients.get(context).get
  }

  val context1 = mock[ChannelHandlerContext]
  val context2 = mock[ChannelHandlerContext]
  val context3 = mock[ChannelHandlerContext]
  val context4 = mock[ChannelHandlerContext]
  val context5 = mock[ChannelHandlerContext]

  def getClientName(ctx: ChannelHandlerContext) = Map(
    context1 -> "Freewind",
    context2 -> "Lily",
    context3 -> "Mike",
    context4 -> "Jeff",
    context5 -> "Alex"
  ).apply(ctx)

  val serverHandler = serverHandlerFactory()

  def client(contexts: ChannelHandlerContext*) = new {

    contexts.foreach { context =>
      if (!clients.contains(context)) {
        serverHandler.channelActive(context)
      }
    }

    private def singleSend(context: ChannelHandlerContext, event: PairEvent) = {
      serverHandler.channelRead(context, event.toMessage)
    }

    def createOrJoinProject(projectName: String): this.type = {
      singleSend(contexts.head, CreateProjectRequest(projectName, getClientName(contexts.head)))
      contexts.tail.foreach(ctx => singleSend(ctx, JoinProjectRequest(projectName, getClientName(ctx))))
      this
    }

    def shareCaret(): this.type = {
      send(CaretSharingModeRequest)
      this
    }

    def parallel(): this.type = {
      send(ParallelModeRequest)
    }

    def send(events: PairEvent*): this.type = {
      for {
        context <- contexts
        event <- events
      } singleSend(context, event)
      this
    }
    def changeMaster(newName: String): this.type = {
      contexts.foreach { context =>
        singleSend(context, ChangeMasterRequest(newName))
      }
      this
    }

    def beMaster(): this.type = {
      contexts.foreach { context =>
        if (!clients.contains(context)) {
          clients.newClient(context)
        }
        clients.all.foreach(_.isMaster = false)
        dataOf(context).isMaster = true
      }
      this
    }
    def disconnect(): Unit = {
      contexts.foreach(serverHandler.channelInactive)
    }
  }

  def project(name: String) = projects.get(name).get

  def clientId(context: ChannelHandlerContext) = clients.get(context).map(_.id).get

  def clientIdName(context: ChannelHandlerContext) = ClientIdName(clientId(context), getClientName(context))

  def resetMocks(mocks: Any*) = mocks.foreach(mock => org.mockito.Mockito.reset(mock))
}

trait MockEvents {
  val changeContentEventA1 = ChangeContentEvent("eventId1", "/aaa", 10, Seq(Insert(2, "abc")))
  val openTabEvent1 = OpenTabEvent("/aaa")
  val openTabEvent2 = OpenTabEvent("/bbb")
  val openTabEvent3 = OpenTabEvent("/ccc")
  val closeTabEvent = CloseTabEvent("/aaa")

  val changeMasterEvent = ChangeMasterRequest("Lily")

  val moveCaretEvent = MoveCaretEvent("/aaa", 10)

  val selectContentEvent = SelectContentEvent("/aaa", 10, 5, ClientIdName("any-id", "any-name"))

  val syncFilesRequest = SyncFilesRequest("any-id", Nil)

  val masterWatchingFiles = GetWatchingFilesFromPair("any-from-id", "any-to-id")
}
