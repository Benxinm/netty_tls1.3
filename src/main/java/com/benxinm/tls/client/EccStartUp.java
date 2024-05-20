package com.benxinm.tls.client;

import com.benxinm.tls.CipherSuite;


public class EccStartUp {
    public static void main(String[] args) {
        new StartUpClient().run(CipherSuite.ECC);
    }
}
