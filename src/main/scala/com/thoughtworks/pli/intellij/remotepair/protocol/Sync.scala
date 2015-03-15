package com.thoughtworks.pli.intellij.remotepair.protocol

import org.json4s.native.Serialization

case object SyncFilesForAll extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class SyncFilesRequest(fromClientId: String, fileSummaries: Seq[FileSummary]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class SyncFileEvent(fromClientId: String, toClientId: String, path: String, content: Content) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class GetWatchingFilesFromPair(fromClientId: String, toClientId: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class WatchingFiles(fromClientId: String, toClientId: String, fileSummaries: Seq[FileSummary]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class MasterWatchingFiles(fromClientId: String, toClientId: String, paths: Seq[String], diffFiles: Int) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class FileSummary(path: String, summary: String)
