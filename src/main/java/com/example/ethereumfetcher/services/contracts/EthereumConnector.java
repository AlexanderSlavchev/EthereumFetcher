package com.example.ethereumfetcher.services.contracts;

import com.example.ethereumfetcher.models.EthereumTransaction;

public interface EthereumConnector {
    String getClientVersion() throws Exception;

    EthereumTransaction getTransactionByHash(String transactionHash);
}