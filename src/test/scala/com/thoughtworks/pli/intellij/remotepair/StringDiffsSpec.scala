package com.thoughtworks.pli.intellij.remotepair

import com.thoughtworks.pli.intellij.remotepair.utils.{Delete, Insert, StringOperation, StringDiff}
import org.specs2.mutable.Specification

class StringDiffsSpec extends Specification {

  "diff" should {
    "return Nil if two strings are the same" in {
      val diffs = StringDiff.findOperations("aaa", "aaa")
      diffs === Seq()
    }
    "return an Insert if only added value" in {
      StringDiff.findOperations("aaa", "aaa11") === Seq(Insert(3, "11"))
      StringDiff.findOperations("aaa", "22aaa") === Seq(Insert(0, "22"))
      StringDiff.findOperations("aaa", "aa33a") === Seq(Insert(2, "33"))
    }
    "return Inserts if only added value" in {
      StringDiff.findOperations("aaa", "aa11a22") === Seq(Insert(2, "11"), Insert(5, "22"))
      StringDiff.findOperations("aaa", "11a22aa") === Seq(Insert(0, "11"), Insert(3, "22"))
      StringDiff.findOperations("aaa", "a11a22a") === Seq(Insert(1, "11"), Insert(4, "22"))
    }
    "return an Delete if only deleted value" in {
      StringDiff.findOperations("aaa", "aa") === Seq(Delete(2, 1))
      StringDiff.findOperations("abc", "bc") === Seq(Delete(0, 1))
      StringDiff.findOperations("abc", "ac") === Seq(Delete(1, 1))
    }
    "return Deletes if only deleted value" in {
      StringDiff.findOperations("abc123", "ab12") === Seq(Delete(2, 1), Delete(4, 1))
      StringDiff.findOperations("abc123", "bc23") === Seq(Delete(0, 1), Delete(2, 1))
      StringDiff.findOperations("abc123", "b2") === Seq(Delete(0, 1), Delete(1, 2), Delete(2, 1))
    }
    "return inserts and deletes if both happens" in {
      StringDiff.findOperations("aaabbb", "bbbccc") === Seq(Delete(0, 3), Insert(3, "ccc"))
      StringDiff.findOperations("aaa123", "b12") === Seq(
        Delete(0, 3),
        Insert(0, "b"),
        Delete(3, 1)
      )
    }
  }

  "applyDiff" should {
    "apply Nil and string will not change" in {
      StringDiff.applyOperations("aaa", Nil) === "aaa"
    }
    "apply inserts to a string" in {
      StringDiff.applyOperations("aaa", Seq(Insert(3, "11"))) === "aaa11"
      StringDiff.applyOperations("aaa", Seq(Insert(0, "22"))) === "22aaa"
      StringDiff.applyOperations("aaa", Seq(Insert(2, "33"))) === "aa33a"
    }
    "apply deletes to a string" in {
      StringDiff.applyOperations("aaa", Seq(Delete(2, 1))) === "aa"
      StringDiff.applyOperations("abc", Seq(Delete(0, 1))) === "bc"
      StringDiff.applyOperations("abc", Seq(Delete(1, 1))) === "ac"
    }
    "apply inserts and deletes to a string" in {
      StringDiff.applyOperations("aaabbb", Seq(Delete(0, 3), Insert(3, "ccc"))) === "bbbccc"
      StringDiff.applyOperations("aaa123", Seq(
        Delete(0, 3),
        Insert(0, "b"),
        Delete(3, 1)
      )) === "b12"
    }
  }

//  "adjustAndMergeDiffs" should {
//    "inserts in 1st diffs will affect all diffs in 2rd whose position is >= them by simply increase the position which is the length of new texts" in {
//      val diffs1 = Seq(Insert(3, "aaa"), Insert(20, "bbbb"))
//      val diffs2 = Seq(Insert(0, "1"), Insert(3, "66"), Delete(8, 3), Insert(12, "222"), Insert(50, "333"))
//      StringDiff.adjustAndMergeDiffs(diffs1, diffs2) ==== Seq(
//        Insert(3, "aaa"), Insert(20, "bbbb"),
//        Insert(0, "1"), Insert(6, "66"), Delete(11, 3), Insert(15, "222"), Insert(57, "333")
//      )
//    }
//    "re-adjust adjusted diffs to make sure they won't have delete/insert at the same position" in {
//      val diffs1 = Seq(Delete(10, 100))
//      val diffs2 = Seq(Insert(10, "aaa"), Insert(20, "bbb"), Delete(30, 3), Delete(40, 3), Insert(50, "ccc"))
//      StringDiff.adjustAndMergeDiffs(diffs1, diffs2) ==== Seq(
//        Delete(10, 100),
//        Insert(10, "aaa"), Insert(13, "bbb"), Delete(16, 3), Delete(16, 3), Insert(16, "ccc")
//      )
//    }
//    "re-adjust adjusted diffs to make sure they won't have delete/insert with overlays" in {
//      val diffs1 = Seq(Delete(10, 100))
//      val diffs2 = Seq(Insert(8, "aaaa"), Insert(20, "bbb"))
//      StringDiff.adjustAndMergeDiffs(diffs1, diffs2) ==== Seq(
//        Delete(10, 100),
//        Insert(8, "aaaa"), Insert(12, "bbb")
//      )
//    }
//    "deletes in 1st diffs will affect all diffs in 2rd whose position is > them by decrease the position but have not overlay" in {
//      val diffs1 = Seq(Delete(3, 3), Delete(10, 3), Delete(20, 4))
//      val diffs2 = Seq(Insert(0, "1"), Insert(3, "66"), Insert(6, "7"), Delete(11, 4), Insert(15, "222"), Delete(50, 3))
//      StringDiff.adjustAndMergeDiffs(diffs1, diffs2) ==== Seq(
//        Delete(3, 3), Delete(10, 3), Delete(20, 4),
//        Insert(0, "1"), Insert(3, "66"), Insert(5, "7"), Delete(8, 4), Insert(10, "222"), Delete(40, 3)
//      )
//    }
//    "handling multiple replace all operations correctly" in {
//      val base = "aaabbb"
//      val diffs1 = StringDiff.findOperations(base, "1111bb")
//      val diffs2 = StringDiff.findOperations(base, "2222bb")
//      val adjusted = StringDiff.adjustAndMergeDiffs(diffs1, diffs2)
//      StringDiff.applyOperations(base, adjusted) === "11112222"
//    }
//    "handle complex operations on the same string" in {
//      val base = "aaabbb"
//      val diffs = Seq("dfwefwef", "sdfoiwe", "aaabbb", "aaabbbccc", "wefaaabbb", "efio2sdf", "asdfdsfhoiho23j98sdjfdf", "df")
//        .map(StringDiff.findOperations(base, _))
//      val allAdjusted = diffs.foldLeft(Seq.empty[StringOperation])((result, current) => StringDiff.adjustAndMergeDiffs(result, current))
//
//      StringDiff.applyOperations(base, allAdjusted) === "dfwefwefsdfoiwecccwefefio2sdfsdfdsfhoiho23j98sdjfdfdf"
//    }
//  }

}
