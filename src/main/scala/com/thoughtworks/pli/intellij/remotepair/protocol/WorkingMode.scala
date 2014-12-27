package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

abstract class WorkingModeEvent extends PairEvent

case object CaretSharingModeRequest extends WorkingModeEvent {
  override def toJson = Serialization.write(this)
}

case object ParallelModeRequest extends WorkingModeEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeModeEvent(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}
