package com.benxinm.tls.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLSession;
import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                future -> {
                    if(future.isSuccess()){
                        System.out.println("握手成功");
                        SSLSession ss =  ctx.pipeline().get(SslHandler.class).engine().getSession();
                        System.out.println("protocol:"+ss.getProtocol());
                        System.out.println("cipherSuite:"+ss.getCipherSuite());
                        System.out.println(ss.getCreationTime());
                    }else{
                        System.out.println("握手失败");
                    }
                }
        );
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" conn:");
        System.out.println("IP:" + channel.localAddress().getHostString());
        System.out.println("Port:" + channel.localAddress().getPort());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String content = (String) msg;
        System.out.println(content);
        System.out.println("=====================");
        ctx.writeAndFlush("This is server");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
