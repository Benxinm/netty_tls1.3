package com.benxinm.tls.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.util.HashMap;
import java.util.Iterator;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class);
        System.out.println("connect to server");
        String word = "Hello Server, this is client";
        ctx.write(word);
        ctx.flush().newSucceededFuture().addListener(
                future -> {
                    if (future.isSuccess()){
                        System.out.println("Successfully send message to server");
                    }else{
                        System.out.println("Failed to send message");
                    }
                }
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive from server: " + (String) msg);
//        ctx.channel().close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
