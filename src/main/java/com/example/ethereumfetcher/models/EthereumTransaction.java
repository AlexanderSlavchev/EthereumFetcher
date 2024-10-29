package com.example.ethereumfetcher.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class EthereumTransaction {
    @Id
    private String transactionHash;

    private int transactionStatus;
    private String blockHash;
    private BigInteger blockNumber;
    private String fromAddress;
    private String toAddress;
    private String contractAddress;
    private int logsCount;
    @Column(columnDefinition = "TEXT")
    private String input;
    private String value;

}