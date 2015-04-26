package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case object DiagnosticRequest extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class DiagnosticInfo(version: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}
