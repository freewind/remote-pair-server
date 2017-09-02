package in.freewind.intellij.remotepair.utils

import in.freewind.intellij.remotepair.GoogleDiffMatchPatch
import in.freewind.intellij.remotepair.GoogleDiffMatchPatch.Diff
import org.apache.commons.lang.StringUtils

import scala.collection.JavaConverters._

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

  def convertToAbsoluteOps(ops1: Seq[StringOperation]): Seq[StringOperation] = {
    ops1.foldLeft((Seq.empty[StringOperation], 0)) {
      case ((result, extraOffset), Insert(offset, content)) => (Insert(offset + extraOffset, content) +: result, extraOffset - content.length)
      case ((result, extraOffset), Delete(offset, length)) => (Delete(offset + extraOffset, length) +: result, extraOffset + length)
    }._1.reverse
  }

  case class Range(start: Int, end: Int)

  def splitOps(ops: Seq[StringOperation], offsets: Seq[Int]): Seq[StringOperation] = {
    ops.flatMap {
      case op@Delete(offset, length) =>
        val validOffsets = Seq(op.offset) ++ offsets.filter(offset => op.offset < offset && offset < op.end) ++ Seq(op.end)
        validOffsets.zip(validOffsets.drop(1)).map { case (start, end) => Delete(start, end - start) }
      case other => Seq(other)
    }
  }

  def clearOps(ops1: Seq[StringOperation], ops2: Seq[StringOperation]) = ops2.filterNot(ops1.contains)

  def adjustOps2(splitOps1: Seq[StringOperation], splitOps2: Seq[StringOperation]): Seq[StringOperation] = {
    sealed trait Op {
      val op: StringOperation
    }
    case class LeftOp(op: StringOperation) extends Op
    case class RightOp(op: StringOperation) extends Op

    val ops: Seq[Op] = (splitOps1.map(LeftOp) ++ splitOps2.map(RightOp)).sortWith {
      case (op1, op2) if op1.op.offset == op2.op.offset => (op1, op2) match {
        case _ if op1.op.isInstanceOf[Insert] && op2.op.isInstanceOf[Delete] => true
        case (LeftOp(op1: Insert), RightOp(op2: Insert)) => true
        case _ => false
      }

      case (op1, op2) => op1.op.offset < op2.op.offset
    }

    println("###ops: " + ops)
    ops.foldLeft((Seq.empty[StringOperation], 0)) {
      case ((result, extraOffset), LeftOp(Delete(offset, length))) => (result, extraOffset - length)
      case ((result, extraOffset), LeftOp(Insert(offset, content))) => (result, extraOffset + content.length)
      case ((result, extraOffset), RightOp(Delete(offset, length))) => (Delete(offset + extraOffset, length) +: result, extraOffset - length)
      case ((result, extraOffset), RightOp(Insert(offset, content))) => (Insert(offset + extraOffset, content) +: result, extraOffset + content.length)
    }._1.reverse
  }

  //  private def toIncrementOps(absoluteOps: Seq[StringOperation]): Seq[StringOperation] = {
  //    absoluteOps.foldLeft((Seq.empty[StringOperation], 0)) {
  //      case ((result, extraOffset), op) =>
  //    }
  //  }

  def adjustLaterOps(ops1: Seq[StringOperation], ops2: Seq[StringOperation]): Seq[StringOperation] = {
    val (absoluteOps1, absoluteOps2) = (convertToAbsoluteOps(ops1), convertToAbsoluteOps(ops2))
    val offsets = findPoints(absoluteOps1, absoluteOps2)
    val (splitOps1, splitOps2) = (splitOps(absoluteOps1, offsets), splitOps(absoluteOps2, offsets))
    val clearOps2 = clearOps(splitOps1, splitOps2)
    val adjusted = adjustOps2(splitOps1, clearOps2)
    //    toIncrementOps(adjusted)
    merge(adjusted)
  }

  private def merge(ops: Seq[StringOperation]): Seq[StringOperation] = {
    ops.toList match {
      case Delete(offset1, length1) :: Delete(offset2, length2) :: t if offset1 == offset2 => merge(Delete(offset1, length1 + length2) :: t)
      case Insert(offset1, content1) :: Insert(offset2, content2) :: t if offset1 + content1.length == offset2 =>
        merge(Insert(offset1, content1 + content2) :: t)
      case h :: t => h :: merge(t).toList
      case others => others
    }
  }

  def findPoints(absoluteOps1: Seq[StringOperation], absoluteOps2: Seq[StringOperation]): Seq[Int] = {
    (absoluteOps1 ++ absoluteOps2).flatMap {
      case op: Delete => Seq(op.offset, op.end)
      case Insert(offset, _) => Seq(offset)
    }.distinct.sorted
  }
  case class AdjustedOperations(newOps: Seq[StringOperation], extraOffsetForFollowing: Int = 0)

  private def isOrdered(offsets: Seq[Int]) = offsets.sorted == offsets
  def adjustLaterOps2(ops1: Seq[StringOperation], ops2: Seq[StringOperation]): Seq[StringOperation] = {
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
    val diffsWithPosition = diffs.asScala.foldLeft(List.empty[(Diff, Int)]) {
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
  val end: Int
}

case class Insert(offset: Int, content: String) extends StringOperation {
  val length = content.length
  val end = offset + length
}

case class Delete(offset: Int, length: Int) extends StringOperation {
  val end = offset + length
}

