package com.thoughtworks.pli.intellij.remotepair.server

import com.thoughtworks.pli.intellij.remotepair.MySpecification
import com.thoughtworks.pli.intellij.remotepair.protocol.{AskForJoinProject, ProtocolMocking}

class ServerHandlerProviderSpec extends MySpecification {

  "When client is connected, server" should {
    "ask for client information" in new ProtocolMocking {
      client(context1)
      there was one(context1).writeAndFlush(AskForJoinProject(None).toMessage)
    }
  }

  "ServerHandler" should {
    "add the context to global cache when channelActive" in new ProtocolMocking {
      client(context1)
      handler.clients.size === 1
    }
    "remove the context from global cache when channel is inactive" in new ProtocolMocking {
      client(context1)
      handler.channelInactive(context1)
      handler.clients.size === 0
    }
  }

}
