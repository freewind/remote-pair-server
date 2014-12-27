package com.thoughtworks.pli.intellij.remotepair

import com.thoughtworks.pli.intellij.remotepair.utils.{Delete, Insert, ContentDiff}
import org.json4s.JsonAST.{JInt, JString, JField, JObject}
import org.json4s._
import org.json4s.ext.EnumNameSerializer

package object protocol {

  implicit val formats = DefaultFormats + new EnumNameSerializer(WorkingMode) + new ContentDiffSerializer

  class ContentDiffSerializer extends CustomSerializer[ContentDiff](format => ( {
    case JObject(JField("op", JString("insert")) :: JField("offset", JInt(offset)) :: JField("content", JString(content)) :: Nil) =>
      Insert(offset.toInt, content)
    case JObject(JField("op", JString("delete")) :: JField("offset", JInt(offset)) :: JField("length", JInt(length)) :: Nil) =>
      Delete(offset.toInt, length.toInt)
  }, {
    case x: ContentDiff => x match {
      case Insert(offset, content) => JObject(JField("op", JString("insert")), JField("offset", JInt(offset)), JField("content", JString(content)))
      case Delete(offset, length) => JObject(JField("op", JString("delete")), JField("offset", JInt(offset)), JField("length", JInt(length)))
    }
  }))

}
