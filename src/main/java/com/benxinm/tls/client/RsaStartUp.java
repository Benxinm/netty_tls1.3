package com.benxinm.tls.client;

import com.benxinm.tls.CipherSuite;

public class RsaStartUp {
    public static void main(String[] args) {
        new StartUpClient().run(CipherSuite.RSA);
    }
}
