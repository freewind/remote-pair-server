package com.thoughtworks.pli.intellij.remotepair.utils

trait PathSupport {

  def isSubPathOf(subPath: String, parentPath: String): Boolean = {
    subPath == parentPath || subPath.startsWith(parentPath + "/")
  }

}
