package com.thoughtworks.pli.intellij.remotepair.utils

import com.thoughtworks.pli.intellij.remotepair.GoogleDiffMatchPatch
import com.thoughtworks.pli.intellij.remotepair.GoogleDiffMatchPatch.Diff
import org.apache.commons.lang.StringUtils

import scala.collection.JavaConversions._

object StringDiff {

  def findOperations(original: String, modified: String): Seq[StringOperation] = {
    val result = new GoogleDiffMatchPatch().diff_main(original, modified)
    convertDiffs(result)
  }

  def applyOperations(original: String, diffs: Seq[StringOperation]): String = {
    diffs.foldLeft(original) {
      case (result, Insert(offset, content)) =>
        val (h, t) = result.splitAt(offset)
        h + content + t
      case (result, Delete(offset, length)) =>
        val (h, t) = result.splitAt(offset)
        if (length >= t.length) {
          h
        } else {
          h + t.substring(length)
        }
    }
  }

  case class AdjustedOperations(newOps: Seq[StringOperation], extraOffsetForFollowing: Int = 0)

  private def isOrdered(offsets: Seq[Int]) = offsets.sorted == offsets
  def adjustLaterOps(ops1: Seq[StringOperation], ops2: Seq[StringOperation]): Seq[StringOperation] = {
    require(isOrdered(ops1.map(_.offset)), "should be ordered by offset: " + ops1)
    require(isOrdered(ops2.map(_.offset)), "should be ordered by offset: " + ops2)

    def adjust1to1(op1: StringOperation, op2: StringOperation): AdjustedOperations = (op1, op2) match {
      case (op1: Insert, op2: Insert) if op2.offset > op1.offset => AdjustedOperations(Seq(op2.copy(offset = op2.offset + op1.length)))
      case (op1: Insert, op2: Insert) if op2.offset < op1.offset => AdjustedOperations(Seq(op2))
      case (op1: Insert, op2: Insert) if op2.offset == op1.offset =>
        if (op1.content.startsWith(op2.content)) AdjustedOperations(Nil, -op2.length)
        else if (op2.content.startsWith(op1.content)) AdjustedOperations(Seq(Insert(op1.end, StringUtils.removeStart(op2.content, op1.content))), -op1.length)
        else AdjustedOperations(Seq(op2.copy(offset = op2.offset + op1.length)))

      case (op1: Delete, op2: Insert) if op2.offset > op1.offset => AdjustedOperations(Seq(op2.copy(offset = math.max(op1.offset, op2.offset - op1.length))))
      case (op1: Delete, op2: Insert) if op2.offset <= op1.offset => AdjustedOperations(Seq(op2))

      case (op1: Insert, op2: Delete) if op2.end <= op1.offset => AdjustedOperations(Seq(op2))
      case (op1: Insert, op2: Delete) if op2.offset >= op1.offset => AdjustedOperations(Seq(op2.copy(offset = op2.offset + op1.length)))
      case (op1: Insert, op2: Delete) if op2.offset < op1.offset =>
        AdjustedOperations(Seq(Delete(op2.offset, op1.offset - op2.offset), Delete(op1.end, op2.end - op1.offset)))

      case (op1: Delete, op2: Delete) if op2.end <= op1.offset => AdjustedOperations(Seq(op2))
      case (op1: Delete, op2: Delete) if op2.offset >= op1.end => AdjustedOperations(Seq(op2.copy(offset = op2.offset - op1.length)))
      case (op1: Delete, op2: Delete) if op2.offset < op1.offset && op2.end <= op1.end => {
        val newLength = op1.offset - op2.offset
        AdjustedOperations(Seq(Delete(op2.offset, newLength)), op2.length - newLength)
      }
      case (op1: Delete, op2: Delete) if op2.offset < op1.offset && op2.end > op1.end => {
        val newLength = op2.length - op1.length
        AdjustedOperations(Seq(Delete(op2.offset, newLength)), op2.length - newLength)
      }
      case (op1: Delete, op2: Delete) if op2.offset >= op1.offset && op2.end <= op1.end => AdjustedOperations(Nil, op2.length)
      case (op1: Delete, op2: Delete) if op2.offset >= op1.offset && op2.end > op1.end => {
        val newLength = op2.end - op1.end
        AdjustedOperations(Seq(Delete(op1.offset, newLength)), op2.length - newLength)
      }
      case _ => AdjustedOperations(Seq(op2))
    }

    def adjust1toN(op1: StringOperation, ops2: Seq[StringOperation]): Seq[StringOperation] = ops2 match {
      case h :: t => adjust1to1(op1, h) match {
        case AdjustedOperations(ops, extraOffset) => ops ++ adjust1toN(op1, t.map {
          case Insert(offset, content) => Insert(offset + extraOffset, content)
          case Delete(offset, length) => Delete(offset + extraOffset, length)
        })
      }
      case Nil => Nil
    }

    ops1.foldLeft(ops2) {
      case (Nil, _) => Nil
      case (ops2, op1) => adjust1toN(op1, ops2)
    }
  }

  def adjustLaterOps_old(diffs1: Seq[StringOperation], diffs2: Seq[StringOperation]): Seq[StringOperation] = {
    def adjust(diff: StringOperation) = diffs1.foldLeft(diff) {
      case (op: Insert, Insert(offset, content)) if op.offset >= offset => op.copy(offset = op.offset + content.length)
      case (op: Delete, Insert(offset, content)) if op.offset >= offset => op.copy(offset = op.offset + content.length)
      case (op: Insert, Delete(offset, length)) if op.offset >= offset => op.copy(offset = math.max(offset, op.offset - length))
      case (op: Delete, Delete(offset, length)) if op.offset >= offset => op.copy(offset = math.max(offset, op.offset - length))
      case (finalDiff, _) => finalDiff
    }
    val adjusted2 = diffs2.map(adjust)
    flatOperationsOnSamePosition(adjusted2)
  }

  def adjustAndMergeDiffs(diffs1: Seq[StringOperation], diffs2: Seq[StringOperation]): Seq[StringOperation] = {
    diffs1 ++: adjustLaterOps(diffs1, diffs2)
  }

  private def flatOperationsOnSamePosition(diffs: Seq[StringOperation]): Seq[StringOperation] = {
    diffs.foldLeft(List.empty[StringOperation]) {
      case (Nil, current) => current :: Nil
      case (list@(prev :: _), current) => (current, prev) match {
        case (op@Insert(offset2, content2), Insert(offset1, content1)) if offset2 <= offset1 + content1.length => op.copy(offset = offset1 + content1.length) :: list
        case (op@Delete(offset2, _), Insert(offset1, content1)) if offset2 <= offset1 + content1.length => op.copy(offset = offset1 + content1.length) :: list
        case (op@Insert(offset2, content2), Delete(offset1, length1)) if offset2 < offset1 => op.copy(offset = offset1) :: list
        case (op@Delete(offset2, _), Delete(offset1, length1)) if offset2 < offset1 => op.copy(offset = offset1) :: list
        case _ => current :: list
      }
    }.reverse
  }

  private def convertDiffs(diffs: java.util.LinkedList[GoogleDiffMatchPatch.Diff]): Seq[StringOperation] = {
    val diffsWithPosition = diffs.toList.foldLeft(List.empty[(Diff, Int)]) {
      case (Nil, item) => (item, 0) :: Nil
      case (list@((prev, position) :: _), item) =>
        if (prev.operation == GoogleDiffMatchPatch.Operation.DELETE) {
          (item, position) :: list
        } else {
          (item, position + prev.text.length) :: list
        }
    }.reverse

    diffsWithPosition.filter(_._1.operation != GoogleDiffMatchPatch.Operation.EQUAL).map {
      case (diff, position) =>
        if (diff.operation == GoogleDiffMatchPatch.Operation.INSERT)
          Insert(position, diff.text)
        else
          Delete(position, diff.text.length)
    }
  }
}

sealed trait StringOperation {
  val offset: Int
}

case class Insert(offset: Int, content: String) extends StringOperation {
  def length = content.length
  def end = offset + length
}

case class Delete(offset: Int, length: Int) extends StringOperation {
  def end = offset + length
}

