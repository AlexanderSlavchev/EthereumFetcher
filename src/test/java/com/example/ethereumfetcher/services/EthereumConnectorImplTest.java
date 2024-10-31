package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.EthereumTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EthereumConnectorImplTest {

    private EthereumConnectorImpl ethereumConnector;
    private Web3j web3Mock;
    private String validHex;
    private String inValidHex;

    @BeforeEach
    void setUp() {
        web3Mock = mock(Web3j.class);
        ethereumConnector = new EthereumConnectorImpl();
        validHex = "0xaa3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0";
        inValidHex = "0xcc3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0";

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
        when(web3Mock.ethGetTransactionByHash(validHex))
                .thenReturn((Request) mockRequest);
        when(mockRequest.send()).thenReturn(mockEthTransaction);
        when(mockEthTransaction.getTransaction()).thenReturn(mockOptionalTransaction);

        when(mockTransaction.getValue()).thenReturn(BigInteger.TEN);
        when(mockTransaction.getHash()).thenReturn(validHex);
        when(mockTransaction.getFrom()).thenReturn("0x1234567890abcdef");
        when(mockTransaction.getTo()).thenReturn("0xfedcba0987654321");
        when(mockTransaction.getBlockHash()).thenReturn("0xabcdef1234567890");
        when(mockTransaction.getBlockNumber()).thenReturn(BigInteger.ONE);
        when(mockTransaction.getInput()).thenReturn("0x");

        EthGetTransactionReceipt mockEthGetTransactionReceipt = mock(EthGetTransactionReceipt.class);
        TransactionReceipt mockReceipt = mock(TransactionReceipt.class);
        Optional<TransactionReceipt> mockOptionalReceipt = Optional.of(mockReceipt);

        Request<?, EthGetTransactionReceipt> mockReceiptRequest = mock(Request.class);
        when(web3Mock.ethGetTransactionReceipt(validHex))
                .thenReturn((Request) mockReceiptRequest);
        when(mockReceiptRequest.send()).thenReturn(mockEthGetTransactionReceipt);
        when(mockEthGetTransactionReceipt.getTransactionReceipt()).thenReturn(mockOptionalReceipt);

        when(mockReceipt.isStatusOK()).thenReturn(true);
        when(mockReceipt.getLogs()).thenReturn(Collections.emptyList());
        when(mockReceipt.getContractAddress()).thenReturn("0xcontractAddress");

        EthereumTransaction transaction = ethereumConnector.getTransactionByHash(validHex);

        assertNotNull(transaction, "Transaction should not be null for valid hash");
        assertEquals("10", transaction.getValue(), "Transaction value should be '10'");
        assertEquals("0xcontractAddress", transaction.getContractAddress(), "Contract address should match mock value");
    }
    @Test
    void testGetTransactionByHashNotFound() throws Exception {
        // Mock на EthTransaction и празен Optional за Transaction
        EthTransaction mockEthTransaction = mock(EthTransaction.class);
        Optional<Transaction> emptyOptionalTransaction = Optional.empty();

        // Mock на заявката за невалиден transaction hash
        Request<?, EthTransaction> mockRequest = mock(Request.class);
        when(web3Mock.ethGetTransactionByHash(inValidHex)).thenReturn((Request) mockRequest);
        when(mockRequest.send()).thenReturn(mockEthTransaction);
        when(mockEthTransaction.getTransaction()).thenReturn(emptyOptionalTransaction);

        // Извикване на тествания метод с невалиден хеш
        EthereumTransaction transaction = ethereumConnector.getTransactionByHash(inValidHex);

        // Проверка, че върнатата стойност е null при невалиден хеш
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