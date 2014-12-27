package com.thoughtworks.pli.intellij.remotepair

import org.json4s.native.Serialization
import JsonFormats.formats

case class SelectContentEvent(path: String, offset: Int, length: Int) extends PairEvent {
  override def toJson = Serialization.write(this)
}
