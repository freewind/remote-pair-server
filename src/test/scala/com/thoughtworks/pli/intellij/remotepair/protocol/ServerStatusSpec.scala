//package com.thoughtworks.pli.intellij.remotepair.protocol
//
//import com.thoughtworks.pli.intellij.remotepair.MySpecification
//
//class ServerStatusSpec extends MySpecification {
//  "ServerStatusResponse" should {
//    "be sent automatically when there is new client joined a project" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      there was atLeastOne(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when client updated info" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      there was atLeastOne(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when client changed to caret sharing mode" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test").shareCaret()
//      there was atLeastOne(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when client changed to parallel mode" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test").parallel()
//      there was atLeastOne(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when master changed" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//      client(context1).send(ChangeMasterRequest("Lily"))
//      there was one(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test",
//          Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = false),
//            ClientInfoResponse(clientId(context2), "test", "Lily", isMaster = true)),
//          Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when client disconnected" in new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//
//      resetMocks(context1)
//
//      serverHandler.channelInactive(context2)
//      there was one(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "be sent automatically when watching files changed" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//
//      client(context1).send(WatchFilesRequest(Seq("/aaa")))
//      there was one(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Seq("/aaa"), WorkingMode.CaretSharing)),
//        freeClients = 0
//      ).toMessage)
//    }
//    "contain free clients" in new ProtocolMocking {
//      client(context1, context2)
//      client(context1).createOrJoinProject("test")
//      there was atLeastOne(context1).writeAndFlush(ServerStatusResponse(
//        Seq(ProjectInfoData("test", Seq(ClientInfoResponse(clientId(context1), "test", "Freewind", isMaster = true)), Nil, WorkingMode.CaretSharing)),
//        freeClients = 1
//      ).toMessage)
//    }
//  }
//
//  "WatchFilesRequest" should {
//    "store the files on server" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test").send(WatchFilesRequest(Seq("/aaa", "/bbb")))
//
//      project("test").watchFiles === Seq("/aaa", "/bbb")
//    }
//  }
//
//}
