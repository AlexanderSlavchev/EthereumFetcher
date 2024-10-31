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

    public static boolean isValidHex(String txHash) {
        if (txHash == null) {
            return false;
        }
        if (txHash.startsWith("0x") || txHash.startsWith("0X")) {
            txHash = txHash.substring(2);
        }
        return txHash.matches("^[0-9a-fA-F]+$");
    }



}
