package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case class InvalidOperationState(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}
