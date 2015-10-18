package com.thoughtworks.pli.intellij.remotepair.server

import io.netty.channel.ChannelHandlerContext
import scala.collection.mutable

class Clients {
  val contexts: mutable.Map[ChannelHandlerContext, Client] = mutable.LinkedHashMap.empty[ChannelHandlerContext, Client]

  def newClient(context: ChannelHandlerContext): Client = synchronized {
    val data = new Client(context)
    contexts.put(context, data)
    data
  }

  def contains(context: ChannelHandlerContext): Boolean = synchronized(contexts.contains(context))

  def removeClient(context: ChannelHandlerContext): Unit = synchronized(contexts.remove(context))

  def all: List[Client] = synchronized(contexts.values.toList)

  def monitors: List[Client] = synchronized(contexts.values.toList.filter(_.isMonitor))

  def get(context: ChannelHandlerContext): Option[Client] = synchronized(contexts.get(context))

  def size: Int = synchronized(contexts.size)

  def findByUserName(username: String): Option[Client] = synchronized(contexts.values.find(_.name.contains(username)))

  def findById(id: String): Option[Client] = synchronized(contexts.values.find(_.id == id))

}
