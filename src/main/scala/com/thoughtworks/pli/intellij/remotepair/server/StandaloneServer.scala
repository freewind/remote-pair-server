package com.thoughtworks.pli.intellij.remotepair.server

import akka.actor.{Props, ActorSystem}
import com.thoughtworks.pli.intellij.remotepair.ServerLogger
import com.thoughtworks.pli.intellij.remotepair.server.event_handlers.ServerHandlerModule

object StandaloneServer extends ServerHandlerModule {

  def main(args: Array[String]): Unit = {
    lazy val actorSystem = ActorSystem("PairServer")
    actorSystem.actorOf(Props(serverActor), "PairServerActor")

    ServerLogger.info("Remote pair server is started")
  }

}
