package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class ResetTabEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class OpenTabEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class CloseTabEvent(path: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case object ResetTabRequest extends PairEvent {
  override def toJson = Serialization.write(this)
}
