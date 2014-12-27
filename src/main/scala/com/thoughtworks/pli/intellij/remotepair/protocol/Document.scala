package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.utils.ContentDiff
import org.json4s.native.Serialization

case class CreateDocument(path: String, content: Content) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CreateDocumentConfirmation(path: String, version: Int, content: Content) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeContentEvent(eventId: String, path: String, baseVersion: Int, changes: Seq[ContentDiff]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CreateServerDocumentRequest(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeContentConfirmation(forEventId: String, path: String, newVersion: Int, diffs: Seq[ContentDiff]) extends PairEvent {
  override def toJson = Serialization.write(this)
}
