package com.benxinm.tls.jks.server;

import com.benxinm.tls.jks.JksChannelInitializer;
import com.benxinm.tls.server.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;


public class StartUpServerJks {

    public static void main(String[] args) throws Exception{
        new StartUpServerJks().run();
    }

    public void run() throws Exception{
        String basePath = "src/main/resources/jks/";
        File jksServerFile = new File(basePath + "server.jks");
        File jksCaFile = new File(basePath + "ca.jks");
        String psd = "123456";
        String keyPsd = "123456";

        SSLContext sslContext = null;

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(jksServerFile), psd.toCharArray());

        //defaultAlgorithm: SunX509 或者系统属性: ssl.KeyManagerFactory.algorithm
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyPsd.toCharArray());
        /** trustKeyStore */
        KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());

        tks.load(new FileInputStream(jksCaFile), psd.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(tks);

        KeyManager[] keyManagers = kmf.getKeyManagers();
        MyX509KeyManager[] managers = new MyX509KeyManager[keyManagers.length];
        for (int i = 0; i < keyManagers.length; i++) {
            managers[i] = new MyX509KeyManager( (X509ExtendedKeyManager) keyManagers[i]);
        }


        sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(managers, tmf.getTrustManagers(),null);


        SSLEngine sslEngine = sslContext.createSSLEngine();
        //是否客户端模式 - 服务端模式
        sslEngine.setUseClientMode(false);
        //是否需要验证客户端（双向验证） - 双向验证
        sslEngine.setNeedClientAuth(true);

        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(4);
        try {
            bootstrap.group(bossGroup,eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new JksChannelInitializer(new ServerHandler(),sslEngine))
                    .childOption(ChannelOption.AUTO_CLOSE,true);
            ChannelFuture future = bootstrap.bind(9096).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
