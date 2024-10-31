package com.example.ethereumfetcher.services.contracts;

import com.example.ethereumfetcher.exceptions.InvalidHexException;
import com.example.ethereumfetcher.exceptions.InvalidTransactionException;
import com.example.ethereumfetcher.models.EthereumTransaction;

import java.io.IOException;

public interface EthereumConnector {
    String getClientVersion() throws Exception;

    EthereumTransaction getTransactionByHash(String transactionHash) throws InvalidHexException, InvalidTransactionException;
}