package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

@deprecated
case class AskForJoinProject(message: Option[String] = None) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ProjectOperationFailed(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class JoinedToProjectEvent(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CreateProjectRequest(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class JoinProjectRequest(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class IgnoreFilesRequest(files: Seq[String]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeMasterRequest(clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

