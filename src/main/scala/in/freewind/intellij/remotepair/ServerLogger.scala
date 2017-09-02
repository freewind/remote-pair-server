package in.freewind.intellij.remotepair

object ServerLogger {

  @volatile var info: String => Unit = scala.Predef.println

}
