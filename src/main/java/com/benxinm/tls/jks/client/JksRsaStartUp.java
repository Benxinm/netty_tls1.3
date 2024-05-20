package com.benxinm.tls.jks.client;

import com.benxinm.tls.CipherSuite;

public class JksRsaStartUp {
    public static void main(String[] args) throws Exception{
        new StartUpClientJks(CipherSuite.RSA).run();
    }
}
