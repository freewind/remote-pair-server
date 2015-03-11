package com.thoughtworks.pli.intellij.remotepair.server

import io.netty.channel.ChannelHandlerContext
import scala.collection.mutable

class Clients {
  val contexts: mutable.Map[ChannelHandlerContext, Client] = mutable.LinkedHashMap.empty[ChannelHandlerContext, Client]

  def newClient(context: ChannelHandlerContext): Client = {
    val data = new Client(context)
    contexts.put(context, data)
    data
  }

  def contains(context: ChannelHandlerContext) = contexts.contains(context)

  def removeClient(context: ChannelHandlerContext) {
    contexts.remove(context)
  }

  def all = contexts.values.toList

  def get(context: ChannelHandlerContext): Option[Client] = contexts.get(context)

  def size = contexts.size

  def findByUserName(username: String): Option[Client] = contexts.map(_._2).find(_.name == Some(username))

  def findById(id: String): Option[Client] = contexts.map(_._2).find(_.id == id)

}
