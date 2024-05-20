package com.benxinm.tls.server;

import com.benxinm.tls.CipherSuite;
import com.benxinm.tls.MyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.handler.ssl.util.TrustManagerFactoryWrapper;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class StartUpServer {
    public static void main(String[] args) {
        try {
            //cipher suite: TLS_AES_128_GCM_SHA256
            //curve: prime256v1
            //private key + x509 cer
            SslContext eccContext = getSslContext(CipherSuite.ECC);
            SslContext rsaContext = getSslContext(CipherSuite.RSA);
            ServerBootstrap bootstrap = new ServerBootstrap();
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
            bootstrap.group(bossGroup,eventExecutors)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new MyChannelInitializer(new ServerHandler(),rsaContext))
                    .bind(9096).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SslContext getSslContext(CipherSuite suite){
        SslContext context = null;
        String keyAddress,cerAddress;
        switch (suite){
            case ECC:
                keyAddress = "src/main/resources/ecc/server8.key";
                cerAddress = "src/main/resources/ecc/server.crt";
                break;
            case RSA:
                keyAddress = "src/main/resources/rsa/server8_rsa.key";
                cerAddress = "src/main/resources/rsa/server_rsa.cer";
                break;
            default:
                keyAddress = "";
                cerAddress = "";
        }
        File privateKey = new File(keyAddress);
        File cer = new File(cerAddress);
        try {
            context = SslContextBuilder.forServer(getKeyManagerFactory())
                    .trustManager(getTrustManagerFactory())
                    .sslProvider(SslProvider.OPENSSL)
                    .clientAuth(ClientAuth.REQUIRE)
                    .protocols("TLSv1.2","TLSv1.3")
                    .build();
//            context = SslContextBuilder.forServer(cer, privateKey)
//                    .sslProvider(SslProvider.OPENSSL)
//                    .protocols("TLSv1.2")
//                    .build();
        }catch (SSLException e) {
            e.printStackTrace();
        }
        return context;
    }


    public static KeyManagerFactory getKeyManagerFactory(){
        String basePath = "src/main/resources/jks/";
        File jksServerFile = new File(basePath + "server.jks");
        String psd = "123456";
        KeyManagerFactory kmf = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(jksServerFile), psd.toCharArray());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, psd.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kmf;
    }

    public static TrustManagerFactory getTrustManagerFactory(){
        String basePath = "src/main/resources/jks/";
        File jksCaFile = new File(basePath + "ca.jks");
        String psd = "123456";
        TrustManagerFactory tmf = null;
        try {
            KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
            tks.load(new FileInputStream(jksCaFile), psd.toCharArray());
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(tks);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tmf;
    }
}
