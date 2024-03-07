package com.benxinm.tls.client;

import com.benxinm.tls.MyChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import java.io.File;

public class StartUpClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(3);
        try {
            File cert = new File("src/main/resources/ecc/client.crt");
            File keyFile = new File("src/main/resources/ecc/client8.key");
            File rootFile = new File("src/main/resources/ecc/ca.crt");
            SslContext sslContext = SslContextBuilder.forClient()
                    .keyManager(cert,keyFile)
                    .trustManager(rootFile)
                    .sslProvider(SslProvider.OPENSSL)
                    .protocols("TLSv1.3").build();
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new MyChannelInitializer(sslContext,new ClientHandler()))
                    .connect("127.0.0.1",9096).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
