package com.benxinm.tls;

import com.benxinm.tls.server.ServerHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;



public class MyChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private SslContext sslContext;
    private ChannelHandler handler;

    public MyChannelInitializer(SslContext sslContext,ChannelHandler handler) {
        this.sslContext = sslContext;
        this.handler = handler;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        channel.pipeline().addFirst(sslContext.newHandler(channel.alloc()));
        channel.pipeline().addLast(new StringEncoder());
        channel.pipeline().addLast(new StringDecoder());
        channel.pipeline().addLast(handler);
    }
}
