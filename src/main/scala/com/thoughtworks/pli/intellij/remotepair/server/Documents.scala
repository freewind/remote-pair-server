package com.thoughtworks.pli.intellij.remotepair.server

import com.thoughtworks.pli.intellij.remotepair._

class Documents {

  private var docs = Map.empty[String, ServerVersionedDocument]

  def update(doc: ServerVersionedDocument, diffs: Seq[ContentDiff]): ServerVersionedDocument = synchronized {
    val newDoc = doc.copy(versions = DocumentVersion(doc.latestVersion + 1, diffs) :: doc.versions)
    docs += doc.path -> newDoc
    newDoc
  }

  def create(createDoc: CreateDocument): ServerVersionedDocument = synchronized {
    val doc = ServerVersionedDocument(createDoc.path, createDoc.content)
    docs += (doc.path -> doc)
    doc
  }

  def find(path: String): Option[ServerVersionedDocument] = synchronized(docs.get(path))

}

case class DocumentVersion(version: Int, changes: Seq[ContentDiff])

case class ServerVersionedDocument(path: String, initContent: Content, versions: List[DocumentVersion] = Nil) {
  def latestChanges: Seq[ContentDiff] = versions.headOption.map(_.changes).getOrElse(Nil)

  def latestVersion = versions.headOption.map(_.version).getOrElse(ServerVersionedDocument.InitVersion)

  def getLaterChangesFromVersion(version: Int): List[ContentDiff] = {
    val matchVersions = versions.reverse.filter(_.version > version)
    checkMatchVersions(version, matchVersions)
    matchVersions.flatMap(_.changes)
  }

  private def checkMatchVersions(version: Int, matchVersions: List[DocumentVersion]): Unit = {
    matchVersions.foldLeft(version) {
      case (v, DocumentVersion(thisVer, _)) => if (thisVer == v + 1)
        thisVer
      else
        throw new RuntimeException(s"Expect version ${v + 1}, get version $thisVer")
    }
  }

  def latestContent = StringDiff.applyDiffs(initContent.text, versions.reverse.flatMap(_.changes))
}

object ServerVersionedDocument {
  val InitVersion = 0
}

