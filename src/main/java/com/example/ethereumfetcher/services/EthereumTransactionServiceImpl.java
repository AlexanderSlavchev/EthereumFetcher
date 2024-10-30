package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.EthereumTransaction;
import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.models.UserTransactions;
import com.example.ethereumfetcher.repositories.TransactionRepository;
import com.example.ethereumfetcher.repositories.UserRepository;
import com.example.ethereumfetcher.repositories.UserTransactionsRepository;
import com.example.ethereumfetcher.services.contracts.EthereumConnector;
import com.example.ethereumfetcher.services.contracts.EthereumTransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EthereumTransactionServiceImpl implements EthereumTransactionService {

    private static final Logger logger = LogManager.getLogger(EthereumTransactionServiceImpl.class);

    @Autowired
    private EthereumConnector ethereumConnector;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTransactionsRepository userTransactionsRepository;

    @Override
    @Transactional
    public List<EthereumTransaction> fetchAndSaveTransactions(List<String> transactionHashes) {
        for (String hash : transactionHashes) {
            logger.info("Processing transaction hash: " + hash);
            Optional<EthereumTransaction> existingTransaction = transactionRepository.findById(hash);

            if (!existingTransaction.isPresent()) {
                EthereumTransaction transaction = ethereumConnector.getTransactionByHash(hash);
                if (transaction != null) {
                    transactionRepository.save(transaction);
                    User currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                            instanceof User ? (User) SecurityContextHolder.getContext()
                            .getAuthentication().getPrincipal() : null;
                    if (currentUser != null) {
                        addTransactionForUserIfNotExists(currentUser.getId(), hash);
                    }
                    logger.info("Saved transaction: " + hash);
                } else {
                    logger.info("Transaction not found on Ethereum: " + hash);
                }
            } else {
                logger.info("Transaction already exists in the database: " + hash);
            }
        }
        return transactionRepository.findAllById(transactionHashes);
    }

    @Override
    public List<EthereumTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional
    public void addTransactionForUserIfNotExists(int userId, String transactionHash) {
        Optional<UserTransactions> existingUserTransaction = userTransactionsRepository.findByUserIdAndTransactionHash(userId, transactionHash);

        if (!existingUserTransaction.isPresent()) {
            EthereumTransaction transaction = ethereumConnector.getTransactionByHash(transactionHash);

            if (transaction != null) {
                transactionRepository.save(transaction);

                UserTransactions userTransaction = new UserTransactions();
                userTransaction.setUserId(userId);
                userTransaction.setTransactionHash(transactionHash);
                userTransaction.setTimestamp(LocalDateTime.now());
                userTransactionsRepository.save(userTransaction);

                logger.info("Transaction added for user: " + userId);
            } else {
                logger.info("Transaction not found on Ethereum: " + transactionHash);
            }
        } else {
            logger.info("Transaction already exists for user: " + userId);
        }
    }

    public List<EthereumTransaction> getTransactionsByUserId(int userId) {
        List<UserTransactions> userTransactions = userTransactionsRepository.findByUserId(userId);
        return userTransactions.stream()
                .map(ut -> transactionRepository.findById(ut.getTransactionHash()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}