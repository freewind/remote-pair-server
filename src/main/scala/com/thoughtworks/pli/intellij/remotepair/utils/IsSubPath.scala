package com.thoughtworks.pli.intellij.remotepair.utils

class IsSubPath {

  def apply(subPath: String, parentPath: String): Boolean = {
    subPath == parentPath || subPath.startsWith(parentPath + "/")
  }

}
