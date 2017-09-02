package in.freewind.intellij.remotepair.server

import in.freewind.intellij.remotepair.protocol._
import in.freewind.intellij.remotepair.utils.{StringOperation, StringDiff}

class Documents(project: Project) {

  private var trackedClientVersions = Map.empty[String, Map[String, Int]]

  private var docs = Map.empty[String, ServerVersionedDocument]

  def update(doc: ServerVersionedDocument, diffs: Seq[StringOperation], sourceClient: ClientIdName): ServerVersionedDocument = synchronized {
    val newDoc = doc.copy(versions = doc.versions :+ DocumentVersion(doc.latestVersion + 1, diffs, sourceClient))
    docs += doc.path -> newDoc
    newDoc
  }

  def create(creator: ClientIdName, createDoc: CreateDocument): ServerVersionedDocument = synchronized {
    val doc = ServerVersionedDocument(createDoc.path, createDoc.content, ServerVersionedDocument.InitVersion, creator)
    docs += (doc.path -> doc)
    doc
  }

  def remove(path: String): Option[ServerVersionedDocument] = synchronized {
    val doc = find(path)
    docs -= path
    doc
  }

  def find(path: String): Option[ServerVersionedDocument] = synchronized(docs.get(path))

  def allPaths: Seq[String] = synchronized(docs.keys.toSeq)

  def trackClientVersion(path: String, clientId: String, version: Int) = synchronized {
    find(path) foreach { doc =>
      if (trackedClientVersions.get(path).isEmpty) {
        trackedClientVersions += (path -> Map(clientId -> version))
      }

      val idVersionMap = trackedClientVersions.get(path).get
      val newMap = (idVersionMap + (clientId -> version)).filter {
        case (cId, _) => project.members.map(_.id).contains(cId)
      }

      trackedClientVersions += (path -> newMap)
      val minVersion = newMap.values.min
      if (minVersion > doc.initVersion) {
        docs += path -> doc.createBaseOn(minVersion)
      }
    }
  }

  def clearAll(): Unit = synchronized {
    trackedClientVersions = Map.empty
    docs = Map.empty
  }

}

case class DocumentVersion(version: Int, diffs: Seq[StringOperation], sourceClient: ClientIdName)

case class ServerVersionedDocument(path: String, initContent: Content, initVersion: Int, creator: ClientIdName, versions: Seq[DocumentVersion] = Nil) {

  def createBaseOn(version: Int): ServerVersionedDocument = {
    val (below, up) = versions.partition(_.version <= version)
    val newInitText = StringDiff.applyOperations(initContent.text, below.flatMap(_.diffs))
    ServerVersionedDocument(path, Content(newInitText, initContent.charset), version, creator, up)
  }

  def latestChanges: Seq[StringOperation] = versions.lastOption.map(_.diffs).getOrElse(Nil)

  def latestVersion = versions.lastOption.map(_.version).getOrElse(initVersion)

  def getLaterChangesFromVersion(version: Int): Seq[StringOperation] = {
    val matchVersions = versions.filter(_.version > version)
    checkMatchVersions(version, matchVersions)
    matchVersions.flatMap(_.diffs)
  }

  private def checkMatchVersions(version: Int, matchVersions: Seq[DocumentVersion]): Unit = {
    matchVersions.foldLeft(version) {
      case (v, DocumentVersion(thisVer, _, _)) => if (thisVer == v + 1)
        thisVer
      else
        throw new RuntimeException(s"Expect version ${v + 1}, get version $thisVer")
    }
  }

  def latestContent: String = StringDiff.applyOperations(initContent.text, versions.flatMap(_.diffs))

  def createConfirmation() = CreateDocumentConfirmation(this.path, this.latestVersion, Content(latestContent, initContent.charset), creator)

  def createSnapshot(): DocumentSnapshotEvent = DocumentSnapshotEvent(this.path, this.latestVersion, initContent.copy(text = this.latestContent))

}

object ServerVersionedDocument {
  val InitVersion = 0
}

