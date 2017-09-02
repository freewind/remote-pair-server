package in.freewind.intellij.remotepair.utils

import java.security.MessageDigest

class Md5 {
  def apply(text: String): String = {
    MessageDigest.getInstance("MD5").digest(text.getBytes).map("%02X".format(_)).mkString
  }
}
