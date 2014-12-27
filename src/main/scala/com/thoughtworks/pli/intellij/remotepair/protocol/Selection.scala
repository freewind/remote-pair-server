package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class SelectContentEvent(path: String, offset: Int, length: Int) extends PairEvent {
  override def toJson = Serialization.write(this)
}
