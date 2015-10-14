//package com.thoughtworks.pli.intellij.remotepair.protocol
//
//import com.thoughtworks.pli.intellij.remotepair.MySpecification
//
//class BeMasterSpec extends MySpecification {
//
//  "The first member of a project" should {
//    "be the master of the project" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//
//      dataOf(context1).isMaster === true
//      dataOf(context2).isMaster === false
//    }
//  }
//
//  "If a master client is disconnect, it" should {
//    "set the next client as master automatically" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//
//      serverHandler.channelInactive(context1)
//      dataOf(context2).isMaster === true
//    }
//  }
//
//  "If server receives a ChangeMasterEvent, it" should {
//    "change the request client as master" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//
//      client(context1).send(changeMasterEvent)
//
//      dataOf(context1).isMaster === false
//      dataOf(context2).isMaster === true
//    }
//    "response error message if specified name is not exist" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test").send(changeMasterEvent)
//
//      there was one(context1).writeAndFlush(ServerErrorResponse(s"Specified user 'Lily' is not found").toMessage)
//    }
//  }
//
//
//}
