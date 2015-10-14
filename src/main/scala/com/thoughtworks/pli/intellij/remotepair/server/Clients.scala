package com.thoughtworks.pli.intellij.remotepair.server

import akka.actor.ActorRef
import io.netty.channel.ChannelHandlerContext
import scala.collection.mutable

class Clients {
  val contexts: mutable.Map[ActorRef, Client] = mutable.LinkedHashMap.empty[ActorRef, Client]

  def newClient(sender: ActorRef): Client = {
    val data = new Client(sender)
    contexts.put(sender, data)
    data
  }

  def contains(context: ActorRef) = contexts.contains(context)

  def removeClient(context: ActorRef) {
    contexts.remove(context)
  }

  def all = contexts.values.toList

  def get(context: ActorRef): Option[Client] = contexts.get(context)

  def size = contexts.size

  def findByUserName(username: String): Option[Client] = contexts.values.find(_.name.contains(username))

  def findById(id: String): Option[Client] = contexts.values.find(_.id == id)

}
