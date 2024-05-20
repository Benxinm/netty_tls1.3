package com.benxinm.tls.client;

import com.benxinm.tls.CipherSuite;
import com.benxinm.tls.MyChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class StartUpClient {
    public void run(CipherSuite suite){
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(3);
        try {
//            ecc
//            File cert = new File("src/main/resources/ecc/client.crt");
//            File keyFile = new File("src/main/resources/ecc/client8.key");
//            File rootFile = new File("src/main/resources/ecc/ca.crt");
//            rsa
//            File cert = new File("src/main/resources/rsa/client_rsa.cer");
//            File keyFile = new File("src/main/resources/rsa/client8_rsa.key");
//            File rootFile = new File("src/main/resources/rsa/ca_rsa.cer");
//            SslContext sslContext = SslContextBuilder.forClient()
//                    .keyManager(cert,keyFile)
//                    .trustManager(rootFile)
//                    .sslProvider(SslProvider.OPENSSL)
//                    .protocols("TLSv1.2").build();
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(getTrustManagerFactory())
                    .keyManager(getKeyManagerFactory(suite))
                    .protocols("TLSv1.2","TLSv1.3").sslProvider(SslProvider.OPENSSL)
                    .build();
            System.out.println(System.currentTimeMillis());
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new MyChannelInitializer(new ClientHandler(),sslContext))
                    .connect("127.0.0.1",9096).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyManagerFactory getKeyManagerFactory(CipherSuite suite){
        String basePath = "src/main/resources/jks/";
        File jksClientFile = new File(basePath + (suite == CipherSuite.RSA? "client_rsa.jks" : "client_ecc.jks"));
        String psd = "123456";
        KeyManagerFactory kmf = null;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(jksClientFile), psd.toCharArray());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, psd.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kmf;
    }

    public TrustManagerFactory getTrustManagerFactory(){
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
