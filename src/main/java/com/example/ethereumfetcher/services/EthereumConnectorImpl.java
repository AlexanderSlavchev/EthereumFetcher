package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.exceptions.InvalidHexException;
import com.example.ethereumfetcher.exceptions.InvalidTransactionException;
import com.example.ethereumfetcher.models.EthereumTransaction;
import com.example.ethereumfetcher.services.contracts.EthereumConnector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.example.ethereumfetcher.helpers.EthereumConnectorHelper.isValidHex;

@Service
public class EthereumConnectorImpl implements EthereumConnector {

    private static final Logger logger = LoggerFactory.getLogger(EthereumConnectorImpl.class);
    private Web3j web3;

    @Value("${ethereum.node.url}")
    private String ethereumUrl;

    public EthereumConnectorImpl() {
        this.web3 = null;
    }

    @PostConstruct
    void init() {
        this.web3 = Web3j.build(new HttpService(ethereumUrl));
        logger.info("EthereumConnector initialized with node: " + ethereumUrl);
    }

    @Override
    @Retry(name = "ethereumClient", fallbackMethod = "clientVersionFallback")
    public String getClientVersion() throws Exception {
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
        if (web3ClientVersion.hasError()) {
            logger.error("Error fetching client version: " + web3ClientVersion.getError().getMessage());
            throw new RuntimeException("Failed to fetch Ethereum client version.");
        }
        logger.info("Fetched client version: " + web3ClientVersion.getWeb3ClientVersion());
        return web3ClientVersion.getWeb3ClientVersion();
    }

    public String clientVersionFallback(Exception ex) {
        logger.warn("Falling back to default client version due to error: " + ex.getMessage());
        return "Unknown Client Version (Fallback)";
    }

    @Override
    public EthereumTransaction getTransactionByHash(String transactionHash) throws InvalidHexException,InvalidTransactionException {
        logger.info("Fetching transaction for hash: " + transactionHash);
        if (!isValidHex(transactionHash) || transactionHash.length() != 66) {
            throw new InvalidHexException("Invalid transaction hash: " + transactionHash);
        }
        try {
            EthTransaction ethTransaction = web3.ethGetTransactionByHash(transactionHash).send();

            if (ethTransaction.getTransaction().isPresent()) {
                EthereumTransaction transaction = mapToEthereumTransaction(ethTransaction);

                TransactionReceipt receipt = web3.ethGetTransactionReceipt(transactionHash)
                        .send()
                        .getTransactionReceipt()
                        .orElse(null);

                if (receipt != null) {
                    transaction.setTransactionStatus(receipt.isStatusOK() ? 1 : 0);
                    transaction.setLogsCount(receipt.getLogs().size());
                    transaction.setContractAddress(receipt.getContractAddress());
                } else {
                    transaction.setTransactionStatus(0);
                    transaction.setLogsCount(0);
                    transaction.setContractAddress(null);
                }

                logger.info("Transaction found on Ethereum for hash: " + transactionHash);
                return transaction;
            } else {
                logger.info("Transaction not found on Ethereum for hash: " + transactionHash);
                return null;
            }
        } catch (IOException e) {
            logger.error("Error fetching transaction for hash: " + transactionHash, e);
            throw new InvalidTransactionException("Failed to fetch transaction with hash: " + transactionHash, e);
        }
    }

    public EthereumTransaction transactionFallback(String transactionHash, Exception ex) {
        logger.warn("Fallback: Could not fetch transaction for hash: " + transactionHash + " due to error: " + ex.getMessage());
        return null;
    }

    private EthereumTransaction mapToEthereumTransaction(EthTransaction ethTransaction) {
        EthereumTransaction transaction = new EthereumTransaction();
        transaction.setTransactionHash(ethTransaction.getTransaction().get().getHash());
        transaction.setFromAddress(ethTransaction.getTransaction().get().getFrom());
        transaction.setToAddress(ethTransaction.getTransaction().get().getTo());
        transaction.setBlockHash(ethTransaction.getTransaction().get().getBlockHash());
        transaction.setBlockNumber(ethTransaction.getTransaction().get().getBlockNumber());
        transaction.setInput(ethTransaction.getTransaction().get().getInput());
        transaction.setValue(ethTransaction.getTransaction().get().getValue().toString());
        logger.info("Mapped EthereumTransaction: " + transaction);
        return transaction;
    }
}