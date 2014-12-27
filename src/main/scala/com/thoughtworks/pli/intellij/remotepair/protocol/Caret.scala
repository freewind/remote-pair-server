package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class MoveCaretEvent(path: String, offset: Int) extends PairEvent {
  override def toJson = Serialization.write(this)
}
