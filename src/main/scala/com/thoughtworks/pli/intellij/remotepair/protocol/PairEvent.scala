package com.thoughtworks.pli.intellij.remotepair.protocol

trait PairEvent {
  def toJson: String

  def toMessage: String = s"$eventName $toJson\n"
  private def eventName: String = getClass.getSimpleName.takeWhile(_ != '$').mkString
}

case class Content(text: String, charset: String)
