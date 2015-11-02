package com.thoughtworks.pli.intellij.remotepair.utils

import org.specs2.mutable.Specification

class AdjustedStringOpsSpec extends Specification {

  "如果两人几乎同时对同一份文档做了一个操作,后者的操作可能需要调整,才能在前者之后应用到文档上" should {
    "对于两者都是删作操作,第二个人的操作可能会被调整" should {
      "两人删除的完全相等,则第二个人会被调整为空" in {
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(10, 5))) should beEmpty
      }
      "第二个人删的比第一个人少,则第二个人会被调整为空" in {
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(10, 4))) should beEmpty
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(11, 4))) should beEmpty
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(12, 1))) should beEmpty
      }
      "第二个人删除的跟第一个人有交集,但也有不同部分,则会去掉交集部分" should {
        "如果第二个人操作起点在第一个人之后,则起点也会被调整" in {
          StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(12, 6))) === Seq(Delete(10, 3))
        }
        "如果第二个人操作起点在第一个人之前,则起点不需要被调整" in {
          StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(8, 6))) === Seq(Delete(8, 2))
        }
      }
      "两个人的删除操作没有交集" should {
        "如果第二个人操作起点在第一个人之后,则起点也会被调整" in {
          StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(20, 6))) === Seq(Delete(15, 6))
        }
        "如果第二个人操作起点在第一个人之前,则完全不需要调整" in {
          StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Delete(2, 3))) === Seq(Delete(2, 3))
        }
      }
    }
    "对于两者都是添加操作,第二个人的操作可能会被调整" should {
      "如果两个人添加的内容完全相等,则第二个人操作会被调整为空" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "abc")), Seq(Insert(10, "abc"))) should beEmpty
      }
      "如果第二个人添加内容不属于前几种情况,且起点在第一个人的起点位置或后面,则起点需要调整" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "abc")), Seq(Insert(10, "ab"))) === Seq(Insert(13, "ab"))
      }
      "如果第二个人添加内容不属于前几种情况,且起点在第一个人的起点前面,则完全不需要调整" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "abc")), Seq(Insert(9, "ab"))) === Seq(Insert(9, "ab"))
      }
    }
    "如果第一个人是删除操作,第二个人是添加操作,则后者的内容不需要调整,但起点有可能调整" should {
      "如果后者的起点大于前者的起点,则需要前移,但不会超过前者的起点" in {
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Insert(12, "bc"))) === Seq(Insert(10, "bc"))
      }
      "如果后者的起点等于前者的起点,则不需要调整" in {
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Insert(10, "bc"))) === Seq(Insert(10, "bc"))
      }
      "如果后者的起点小于前者的起点,则不需要调整" in {
        StringDiff.adjustLaterOps(Seq(Delete(10, 5)), Seq(Insert(9, "bc"))) === Seq(Insert(9, "bc"))
      }
    }
    "如果第一个人是添加操作,第二个人是删除操作,则后者的内容不需要调整,但起点有可能调整" should {
      "如果后者的起点大于等于前者的起点,则需要后移" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(11, 5))) === Seq(Delete(13, 5))
        StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(10, 5))) === Seq(Delete(12, 5))
      }
      "如果后者的终点小于等于前者的起点,则不需要调整" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(5, 5))) === Seq(Delete(5, 5))
        StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(5, 4))) === Seq(Delete(5, 4))
      }
      "如果后者的起点小于前者的起点,则起点不需要调整,但要避开前者添加的内容" in {
        StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(9, 5))) === Seq(Delete(9, 1), Delete(11, 4))
      }
    }
  }

  "如果两人几乎同时对同一份文档做了操作,前者做了一个,后者做了多个,后者的操作可能需要调整,才能在前者之后应用到文档上" should {
    "前面是一个添加操作,后面是多个任意操作" in {
      StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Insert(8, "xy"), Delete(11, 5), Insert(20, "op"))) === Seq(
        Insert(8, "xy"), Delete(11, 1), Delete(13, 4), Insert(22, "op")
      )
    }
    "前面是一个添加操作,后面的删除操作会分裂" in {
      StringDiff.adjustLaterOps(Seq(Insert(10, "bc")), Seq(Delete(9, 5), Insert(15, "xy"))) === Seq(
        Delete(9, 1), Delete(11, 4), Insert(17, "xy")
      )
    }
    "前面是一个删除操作,后面是多个任意操作" in {
      StringDiff.adjustLaterOps(Seq(Delete(10, 8)), Seq(Insert(8, "xy"), Delete(11, 5), Insert(20, "op"))) === Seq(
        Insert(8, "xy"), Delete(11, 1), Insert(16, "op")
      )
    }
  }

  "如果两人几乎同时对同一份文档做了操作,前者做了多个,后者做了一个,后者的操作可能需要调整,才能在前者之后应用到文档上" should {
    "前面是多个操作,后者是一个添加操作" in {
      StringDiff.adjustLaterOps(Seq(Insert(8, "xy"), Delete(11, 5), Insert(20, "op")), Seq(Insert(10, "abc"))) === Seq(
        Insert(11, "abc")
      )
    }
    "前面是多个操作,后者是一个删除操作" in {
      StringDiff.adjustLaterOps(Seq(Insert(8, "xy"), Delete(11, 5), Insert(20, "op")), Seq(Delete(10, 8))) === Seq(
        Delete(11, 4)
      )
    }
  }

  "如果两人几乎同时对同一份文档做了多个操作,后者的操作可能需要调整,才能在前者之后应用到文档上" in {
    // hope to improve it, to let the `xy` and `op` still be in the final result
    StringDiff.adjustLaterOps(
      Seq(Insert(8, "xy"), Delete(11, 5), Insert(22, "op")),
      Seq(Insert(3, "abc"), Delete(10, 8), Delete(20, 4), Insert(30, "mn"))
    ) === Seq(Insert(3, "abc"), Delete(10, 1), Delete(12, 2), Delete(24, 4), Insert(34, "mn"))
  }

  // 12345678901234567890123456789012345678901234567890
  // 12345678xy9_____567890123op456789012345678901234567890
  // 123abc4567________678901____67890123456789mn01234567890
  // 123abc4567_xy______567890123op456789012345678901234567890
}
