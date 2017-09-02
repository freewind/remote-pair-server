package in.freewind.intellij.remotepair.utils

import java.util.UUID

class NewUuid {
  def apply(): String = UUID.randomUUID().toString.replace("-", "")
}
