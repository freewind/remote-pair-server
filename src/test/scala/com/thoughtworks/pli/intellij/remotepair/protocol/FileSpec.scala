//package com.thoughtworks.pli.intellij.remotepair.protocol
//
//import com.thoughtworks.pli.intellij.remotepair.MySpecification
//
//class FileSpec extends MySpecification {
//
//  "When server receives file related events, it" should {
//    def shouldBroadcast(event: PairEvent) = new ProtocolMocking {
//      client(context1, context2).createOrJoinProject("test")
//
//      client(context1).send(event, event)
//
//      there was two(context2).writeAndFlush(event.toMessage)
//    }
//    "broadcast to other contexts for CreateFileEvent" in new ProtocolMocking {
//      shouldBroadcast(CreateFileEvent("/aaa", Content("my-content", "UTF-8")))
//    }
//    "broadcast to other contexts for DeleteFileEvent" in new ProtocolMocking {
//      shouldBroadcast(DeleteFileEvent("/aaa"))
//    }
//    "broadcast to other contexts for CreateDirEvent" in new ProtocolMocking {
//      shouldBroadcast(CreateDirEvent("/ddd"))
//    }
//    "broadcast to other contexts for DeleteDirEvent" in new ProtocolMocking {
//      shouldBroadcast(DeleteDirEvent("/ddd"))
//    }
//    "broadcast to other contexts for RenameDirEvent" in new ProtocolMocking {
//      shouldBroadcast(RenameDirEvent("/aaa/bbb", "ccc"))
//    }
//    "broadcast to other contexts for RenameFileEvent" in new ProtocolMocking {
//      shouldBroadcast(RenameFileEvent("/aaa/bbb", "ccc"))
//    }
//    "broadcast to other contexts for MoveDirEvent" in new ProtocolMocking {
//      shouldBroadcast(MoveDirEvent("/aaa/bbb", "/ccc"))
//    }
//    "broadcast to other contexts for MoveFileEvent" in new ProtocolMocking {
//      shouldBroadcast(MoveFileEvent("/aaa/bbb", "/ccc"))
//    }
//  }
//
//  "If server receives a DeleteFileEvent, it" should {
//    "delete corresponding server document if there exists" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      client(context1).send(CreateDocument("/aaa", Content("abc123", "UTF-8")))
//      client(context1).send(DeleteFileEvent("/aaa"))
//      project("test").documents.find("/aaa") ==== None
//    }
//  }
//
//  "If server receives a DeleteDirEvent, it" should {
//    "delete all server documents under this dir if existed" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      client(context1).send(CreateDocument("/aaa/11", Content("abc123", "UTF-8")))
//      client(context1).send(CreateDocument("/aaa/22/33", Content("abc123", "UTF-8")))
//      client(context1).send(CreateDocument("/bbb", Content("abc123", "UTF-8")))
//      client(context1).send(DeleteDirEvent("/aaa"))
//      project("test").documents.find("/aaa/11") ==== None
//      project("test").documents.find("/aaa/22/33") ==== None
//      project("test").documents.find("/bbb") must beSome
//    }
//  }
//
//  "If server receives a MoveDirEvent, it" should {
//    "delete all server documents under source dir if existed" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      client(context1).send(CreateDocument("/aaa/11", Content("abc123", "UTF-8")))
//      client(context1).send(CreateDocument("/aaa/22/33", Content("abc123", "UTF-8")))
//      client(context1).send(CreateDocument("/bbb", Content("abc123", "UTF-8")))
//      client(context1).send(MoveDirEvent("/aaa", "/ccc"))
//      project("test").documents.find("/aaa/11") ==== None
//      project("test").documents.find("/aaa/22/33") ==== None
//      project("test").documents.find("/bbb") must beSome
//    }
//  }
//
//  "If server receives a MoveFileEvent, it" should {
//    "delete corresponding server document for source file if it exists" in new ProtocolMocking {
//      client(context1).createOrJoinProject("test")
//      client(context1).send(CreateDocument("/aaa", Content("abc123", "UTF-8")))
//      client(context1).send(MoveFileEvent("/aaa", "/bbb"))
//      project("test").documents.find("/aaa") ==== None
//    }
//  }
//
//}
