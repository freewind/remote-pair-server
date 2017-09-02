package in.freewind.intellij.remotepair.server

import in.freewind.intellij.remotepair.ServerLogger

object StandaloneServer extends App {

  val port = 8888
  new Server(host = None, port).start()

  ServerLogger.info("Remote pair server is started on: " + port)

}
