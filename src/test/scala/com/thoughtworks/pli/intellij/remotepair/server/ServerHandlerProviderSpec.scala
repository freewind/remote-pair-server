//package com.thoughtworks.pli.intellij.remotepair.server
//
//import com.thoughtworks.pli.intellij.remotepair.MySpecification
//import com.thoughtworks.pli.intellij.remotepair.protocol.ProtocolMocking
//
//class ServerHandlerProviderSpec extends MySpecification {
//
//  "ServerHandler" should {
//    "add the context to global cache when channelActive" in new ProtocolMocking {
//      client(context1)
//      clients.size === 1
//    }
//    "remove the context from global cache when channel is inactive" in new ProtocolMocking {
//      client(context1)
//      serverHandler.channelInactive(context1)
//      clients.size === 0
//    }
//  }
//
//}
