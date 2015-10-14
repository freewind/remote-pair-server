package com.thoughtworks.pli.intellij.remotepair.server

import java.util.UUID

import akka.actor.ActorRef
import com.thoughtworks.pli.intellij.remotepair._
import com.thoughtworks.pli.intellij.remotepair.protocol.PairEvent

case class Client(context: ActorRef) {

  val id = UUID.randomUUID().toString

  @volatile var isMaster = false

  @volatile var name: Option[String] = None

  def writeEvent(event: PairEvent) = {
    ServerLogger.info("Server -> " + name + ": " + event.toMessage)
    context ! event
  }

}
