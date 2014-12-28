package com.thoughtworks.pli.intellij.remotepair.server

import java.util.UUID

import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent
import io.netty.channel.ChannelHandlerContext
import com.thoughtworks.pli.intellij.remotepair._

case class Client(context: ChannelHandlerContext) {

  val id = UUID.randomUUID().toString

  @volatile var isMaster = false

  @volatile var name: Option[String] = None

  val projectSpecifiedLocks = new ProjectSpecifiedLocks

  def writeEvent(event: PairEvent) = {
    ServerLogger.info("Server -> " + name + ": " + event.toMessage)
    context.writeAndFlush(event.toMessage)
  }

}

class ProjectSpecifiedLocks {
  val activeTabLocks = new EventLocks[String]
}
