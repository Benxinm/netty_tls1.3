package com.benxinm.tls.jks.client;

import com.benxinm.tls.CipherSuite;
import com.benxinm.tls.jks.JksChannelInitializer;
import com.benxinm.tls.client.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class StartUpClientJks {
    private CipherSuite suite;

    public StartUpClientJks(CipherSuite suite) {
        this.suite = suite;
    }

    public void run() throws Exception{
        String basePath = "src/main/resources/jks/";
        File jksClientFile = new File(basePath + /*(suite == CipherSuite.ECC? "client_ecc.jks" : "client_rsa.jks")*/ "client.jks");
        File jksCaFile = new File(basePath + "ca.jks");
        String psd = "123456";
        String keyPsd = "123456";
        SSLContext sslContext = null;
        //获取Jks中的Keystore
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(jksClientFile),psd.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks,keyPsd.toCharArray());
        //trustKeyStore
        KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
        tks.load(new FileInputStream(jksCaFile),psd.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(tks);
        //TLS版本协商以及证书密钥以及相信CA注入
        sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);
        //设置双向验证
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(true);

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new JksChannelInitializer(new ClientHandler(),sslEngine));
        ChannelFuture future = bootstrap.connect("127.0.0.1", 9096).sync();
        future.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
    }
}
