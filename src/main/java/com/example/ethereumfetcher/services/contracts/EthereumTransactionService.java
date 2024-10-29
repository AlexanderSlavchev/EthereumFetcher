package com.example.ethereumfetcher.services.contracts;

import com.example.ethereumfetcher.models.EthereumTransaction;

import java.util.List;

public interface EthereumTransactionService {
    List<EthereumTransaction> fetchAndSaveTransactions(List<String> transactionHashes);

    List<EthereumTransaction> getAllTransactions();


    List<EthereumTransaction> getTransactionsByUserId(int userId);

}
