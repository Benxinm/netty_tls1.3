package com.benxinm.tls.server;

import com.benxinm.tls.MyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import java.io.File;

public class StartUpServer {
    public static void main(String[] args) {
        try {
            //cipher suite: TLS_AES_128_GCM_SHA256
            //private key + x509 cer
            File privateKey = new File("src/main/resources/server8.key");
            File cer = new File("src/main/resources/server.cer");
            SslContext sslContext = SslContextBuilder.forServer(cer, privateKey)
                    .sslProvider(SslProvider.OPENSSL)
                    .protocols("TLSv1.3")
                    .build();
            ServerBootstrap bootstrap = new ServerBootstrap();
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
            bootstrap.group(bossGroup,eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyChannelInitializer(sslContext,new ServerHandler()))
                    .bind(9096).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
