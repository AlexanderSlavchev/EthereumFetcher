package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.EthereumTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EthereumConnectorImplTest {

    private EthereumConnectorImpl ethereumConnector;
    private Web3j web3Mock;

    @BeforeEach
    void setUp() {
        web3Mock = mock(Web3j.class);
        ethereumConnector = new EthereumConnectorImpl();

        ReflectionTestUtils.setField(ethereumConnector, "ethereumUrl", "http://localhost:8545");
        ReflectionTestUtils.setField(ethereumConnector, "web3", web3Mock);
    }

    @Test
    void testGetClientVersionSuccess() throws Exception {
        Request<?, Web3ClientVersion> mockRequest = mock(Request.class);
        Web3ClientVersion mockVersion = mock(Web3ClientVersion.class);

        when(web3Mock.web3ClientVersion()).thenReturn((Request) mockRequest);
        when(mockRequest.send()).thenReturn(mockVersion);
        when(mockVersion.hasError()).thenReturn(false);
        when(mockVersion.getWeb3ClientVersion()).thenReturn("TestClientVersion");

        String version = ethereumConnector.getClientVersion();
        assertEquals("TestClientVersion", version, "Client version should match mock version");
    }

    @Test
    void testGetTransactionByHashSuccess() throws Exception {
        EthTransaction mockEthTransaction = mock(EthTransaction.class);
        Transaction mockTransaction = mock(Transaction.class);
        Optional<Transaction> mockOptionalTransaction = Optional.of(mockTransaction);

        Request<?, EthTransaction> mockRequest = mock(Request.class);
        when(web3Mock.ethGetTransactionByHash("validTransactionHash")).thenReturn((Request) mockRequest);
        when(mockRequest.send()).thenReturn(mockEthTransaction);
        when(mockEthTransaction.getTransaction()).thenReturn(mockOptionalTransaction);
        when(mockTransaction.getValue()).thenReturn(BigInteger.TEN);

        EthereumTransaction transaction = ethereumConnector.getTransactionByHash("validTransactionHash");

        assertNotNull(transaction, "Transaction should not be null for valid hash");
        assertEquals("10", transaction.getValue(), "Transaction value should be '10'");
    }

    @Test
    void testGetTransactionByHashNotFound() throws Exception {
        EthTransaction mockEthTransaction = mock(EthTransaction.class);
        Optional<Transaction> emptyOptionalTransaction = Optional.empty();

        Request<?, EthTransaction> mockRequest = mock(Request.class);
        when(web3Mock.ethGetTransactionByHash("invalidTransactionHash")).thenReturn((Request) mockRequest);
        when(mockRequest.send()).thenReturn(mockEthTransaction);
        when(mockEthTransaction.getTransaction()).thenReturn(emptyOptionalTransaction);

        EthereumTransaction transaction = ethereumConnector.getTransactionByHash("invalidTransactionHash");

        assertNull(transaction, "Transaction should be null for invalid hash");
    }

    @Test
    void testGetClientVersionError() {
        try {
            when(web3Mock.web3ClientVersion()).thenThrow(new RuntimeException("Connection error"));
            ethereumConnector.getClientVersion();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertEquals("Connection error", e.getMessage(), "Exception message should match");
        }
    }
}