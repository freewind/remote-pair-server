package com.thoughtworks.pli.intellij.remotepair

import org.json4s.native.Serialization
import JsonFormats.formats

case object CaretSharingModeRequest extends WorkingModeEvent {
  override def toJson = Serialization.write(this)
}
abstract class WorkingModeEvent extends PairEvent


case object ParallelModeRequest extends WorkingModeEvent {
  override def toJson = Serialization.write(this)
}

case class ChangeModeEvent(message: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}
object WorkingMode extends Enumeration {
  val CaretSharing, Parallel = Value
}
