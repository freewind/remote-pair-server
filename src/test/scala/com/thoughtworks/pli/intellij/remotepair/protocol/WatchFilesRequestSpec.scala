//package com.thoughtworks.pli.intellij.remotepair.protocol
//
//import com.thoughtworks.pli.intellij.remotepair.MySpecification
//
//class WatchFilesRequestSpec extends MySpecification {
//
//  "When server receives a WatchFilesRequest, it" should {
//    "response a WatchFilesChangedEvent to all client" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//      client(context1).send(new WatchFilesRequest(Seq("/aaa", "/bbb")))
//      there was one(context2).writeAndFlush(new WatchFilesChangedEvent(Seq("/aaa", "/bbb")).toMessage)
//    }
//  }
//
//}
