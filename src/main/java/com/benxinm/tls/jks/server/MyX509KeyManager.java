package com.benxinm.tls.jks.server;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class MyX509KeyManager extends X509ExtendedKeyManager {

    private final X509ExtendedKeyManager delegate;
    private final String defaultHostName = "1";

    public MyX509KeyManager(X509ExtendedKeyManager delegate) {
        if (delegate == null){
            throw new NullPointerException("delegate");
        }
        this.delegate = delegate;
    }

    @Override
    public String[] getClientAliases(String s, Principal[] principals) {
        return delegate.getClientAliases(s,principals);
    }

    @Override
    public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
        return delegate.chooseClientAlias(strings,principals,socket);
    }

    @Override
    public String[] getServerAliases(String s, Principal[] principals) {
        return delegate.getServerAliases(s,principals);
    }

    @Override
    public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
        System.out.println("null");
        return null;
        //        throw new OperationNotSupportedException();
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        ExtendedSSLSession session = (ExtendedSSLSession) engine.getHandshakeSession();
        String hostname = null;
        for(SNIServerName name : session.getRequestedServerNames()){
            if (name.getType() == StandardConstants.SNI_HOST_NAME){
                hostname = ((SNIHostName) name).getAsciiName();
                break;
            }
        }
        if (hostname != null && (getCertificateChain(hostname) != null) && getPrivateKey(hostname) != null){
            System.out.println(hostname);
            return hostname;
        }else {
            System.out.println("My key manager default");
            return defaultHostName;
        }
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        return delegate.getCertificateChain(s);
    }

    @Override
    public PrivateKey getPrivateKey(String s) {
        return delegate.getPrivateKey(s);
    }
}
