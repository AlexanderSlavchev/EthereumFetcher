package com.example.ethereumfetcher.helpers;

import com.example.ethereumfetcher.services.EthereumConnectorImpl;

public class EthereumConnectorHelper {


    public static void ethereumConnectorTest() {
        try {
            EthereumConnectorImpl connector = new EthereumConnectorImpl();
            System.out.println("Connected to Ethereum client version: " + connector.getClientVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
