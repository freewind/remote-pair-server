package com.thoughtworks.pli.intellij.remotepair.server

import java.nio.charset.Charset

import com.thoughtworks.pli.intellij.remotepair.server.event_handlers.ServerHandlerModule
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}

case class Server(host: Option[String], port: Int) extends ServerHandlerModule {

  private val bossGroup = new NioEventLoopGroup()
  private val workerGroup = new NioEventLoopGroup()
  private val bootstrap = new ServerBootstrap()
  bootstrap.group(bossGroup, workerGroup)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(ChildHandler)
    .option(ChannelOption.SO_BACKLOG.asInstanceOf[ChannelOption[Any]], 128)
    .childOption(ChannelOption.SO_KEEPALIVE.asInstanceOf[ChannelOption[Any]], true)

  private var channel: ChannelFuture = _

  def startAndWait() = try start() finally close()

  def start() = {
    channel = host match {
      case Some(h) => bootstrap.bind(h, port)
      case _ => bootstrap.bind(port)
    }
    channel.sync()
  }

  def close(): ChannelFuture = try channel.channel().close().sync() finally {
    workerGroup.shutdownGracefully()
    bossGroup.shutdownGracefully()
  }

  object ChildHandler extends ChannelInitializer[SocketChannel] {
    override def initChannel(channel: SocketChannel) {
      channel.pipeline().addLast(
        new LineBasedFrameDecoder(Int.MaxValue),
        new StringDecoder(Charset.forName("UTF-8")),
        new StringEncoder(Charset.forName("UTF-8")),
        serverHandlerFactory())
    }
  }

}





