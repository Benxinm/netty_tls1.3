package com.benxinm.tls.jks.client;

import com.benxinm.tls.CipherSuite;

public class JksEccStartUp {
    public static void main(String[] args) throws Exception{
        new StartUpClientJks(CipherSuite.ECC).run();
    }
}
