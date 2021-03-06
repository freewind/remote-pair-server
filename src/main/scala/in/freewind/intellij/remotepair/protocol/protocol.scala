package in.freewind.intellij.remotepair

import in.freewind.intellij.remotepair.utils.{StringOperation, Delete, Insert}
import org.json4s.JsonAST.{JField, JInt, JObject, JString}
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.native.Serialization

package object protocol {

  trait PairEvent

  implicit class RichPairEvent(event: PairEvent) {
    def toJson: String = Serialization.write(event)

    def toMessage: String = s"$eventName $toJson\n"
    private def eventName: String = event.getClass.getSimpleName.takeWhile(_ != '$').mkString
  }

  implicit val formats = DefaultFormats + new EnumNameSerializer(WorkingMode) + new ContentDiffSerializer

  class ContentDiffSerializer extends CustomSerializer[StringOperation](format => ( {
    case JObject(JField("op", JString("insert")) :: JField("offset", JInt(offset)) :: JField("content", JString(content)) :: Nil) =>
      Insert(offset.toInt, content)
    case JObject(JField("op", JString("delete")) :: JField("offset", JInt(offset)) :: JField("length", JInt(length)) :: Nil) =>
      Delete(offset.toInt, length.toInt)
  }, {
    case x: StringOperation => x match {
      case Insert(offset, content) => JObject(JField("op", JString("insert")), JField("offset", JInt(offset)), JField("content", JString(content)))
      case Delete(offset, length) => JObject(JField("op", JString("delete")), JField("offset", JInt(offset)), JField("length", JInt(length)))
    }
  }))

  case class Content(text: String, charset: String)

}
