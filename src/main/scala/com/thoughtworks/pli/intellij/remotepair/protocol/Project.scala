package com.thoughtworks.pli.intellij.remotepair

import org.json4s.native.Serialization
import JsonFormats.formats

case class JoinProjectRequest(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CreateProjectRequest(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class JoinedToProjectEvent(projectName: String, clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class AskForJoinProject(message: Option[String] = None) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case object AskForWorkingMode extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class IgnoreFilesRequest(files: Seq[String]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeMasterEvent(clientName: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

