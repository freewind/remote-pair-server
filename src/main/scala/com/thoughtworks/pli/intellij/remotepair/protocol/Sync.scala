package com.thoughtworks.pli.intellij.remotepair.protocol

import com.thoughtworks.pli.intellij.remotepair.ServerLogger
import org.json4s.native.Serialization

case object SyncFilesForAll extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class SyncFilesRequest(fromClientId: String, fileSummaries: Seq[FileSummary]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class SyncFileEvent(toClientId: String, path: String, content: Content) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class GetPairableFilesFromPair(fromClientId: String) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class PairableFiles(fromClientId: String, toClientId: String, paths: Seq[String]) extends PairEvent {
  override def toJson = Serialization.write(this)
}

case class MasterPairableFiles(toClientId: String, paths: Seq[String]) extends PairEvent {
  // TODO: remove it later
  val invalid = paths.filter(_.startsWith("/Users"))
  if (invalid.nonEmpty) {
    ServerLogger.info("!!!!!!!!!!!!!!!! Found invalid paths:")
    invalid.foreach(ServerLogger.info)
    throw new RuntimeException("!!!!!!!!!!! Found invalid paths")
  }
  override def toJson = Serialization.write(this)
}

case class FileSummary(path: String, summary: String)
