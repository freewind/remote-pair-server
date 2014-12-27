package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class CreateDirEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class DeleteDirEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CreateFileEvent(path: String, content: Content) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class DeleteFileEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class RenameEvent(from: String, to: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}
