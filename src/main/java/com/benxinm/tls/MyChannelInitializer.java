package com.benxinm.tls;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;


import java.util.ArrayList;
import java.util.List;


public class MyChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private List<SslContext> sslContexts = new ArrayList<>();
    private ChannelHandler handler;

    public MyChannelInitializer(ChannelHandler handler,SslContext... sslContexts) {
        for (SslContext context :sslContexts) {
            this.sslContexts.add(context);
        }
        this.handler = handler;
    }

    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        for (SslContext context : sslContexts) {
            channel.pipeline().addLast(context.newHandler(channel.alloc()));
        }
        channel.pipeline().addLast(new StringEncoder());
        channel.pipeline().addLast(new StringDecoder());
        channel.pipeline().addLast(handler);
    }
}
