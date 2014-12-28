package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class ServerStatusResponse(projects: Seq[ProjectInfoData], freeClients: Int) extends PairEvent {
  override def toJson = Serialization.write(this)
  def findProject(name: String) = projects.find(_.name == name)
}

case class ProjectInfoData(name: String, clients: Seq[ClientInfoResponse], ignoredFiles: Seq[String], workingMode: WorkingMode.Value) {
  def isCaretSharing = workingMode == WorkingMode.CaretSharing
}

case class ClientInfoResponse(clientId: String, project: String, name: String, isMaster: Boolean) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ServerErrorResponse(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ServerMessageResponse(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

object WorkingMode extends Enumeration {
  val CaretSharing, Parallel = Value
}
