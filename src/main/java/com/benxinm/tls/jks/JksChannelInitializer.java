package com.benxinm.tls.jks;

import com.benxinm.tls.SelfDecoder;
import com.benxinm.tls.SelfEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class JksChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelHandler handler;
    private SSLEngine engine;

    public JksChannelInitializer(ChannelHandler handler, SSLEngine engine) {
        this.handler = handler;
        this.engine = engine;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        SslHandler sslHandler = new SslHandler(engine);
        sslHandler.sslCloseFuture().addListener(
                future ->{
                   if (future.isSuccess()){
                       System.out.println("Connection close successfully");
                   }else{
                       System.out.println("Connection close fail");
                   }
                });
        channel.pipeline().addFirst("ssl", sslHandler);
        channel.pipeline().addLast(new StringEncoder());
        channel.pipeline().addLast(new StringDecoder());
//        channel.pipeline().addLast(new SelfDecoder());
//        channel.pipeline().addLast(new SelfEncoder());
        channel.pipeline().addLast(handler);
    }
}
