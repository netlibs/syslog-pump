/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package syslog.pump;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class App {
    public static void main(String[] args) throws InterruptedException {
      NioEventLoopGroup bossGroup = new NioEventLoopGroup();
      NioEventLoopGroup workerGroup = new NioEventLoopGroup();
      try {
          ServerBootstrap bootstrap = new ServerBootstrap()
                  .group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) {
                          ch.pipeline().addLast(new EchoServerHandler());
                      }
                  });
          ChannelFuture future = bootstrap.bind(8080).sync();
          future.channel().closeFuture().sync();
      } finally {
          workerGroup.shutdownGracefully();
          bossGroup.shutdownGracefully();
      }
  }
    
    static class EchoServerHandler extends ChannelInboundHandlerAdapter {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("MSG: " + msg);
          ctx.write(msg);
      }
      @Override
      public void channelReadComplete(ChannelHandlerContext ctx) {
          ctx.flush();
      }
      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
          cause.printStackTrace();
          ctx.close();
      }
  }
}