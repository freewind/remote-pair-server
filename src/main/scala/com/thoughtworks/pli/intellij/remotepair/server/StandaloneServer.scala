package com.thoughtworks.pli.intellij.remotepair.server

import com.thoughtworks.pli.intellij.remotepair.ServerLogger

object StandaloneServer extends App {

  val port = 31415
  new Server(host = None, port).start()

  ServerLogger.info("Remote pair server is started on: " + port)

}
