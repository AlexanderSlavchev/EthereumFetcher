package com.example.ethereumfetcher.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "transaction_hash", nullable = false)
    private String transactionHash;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}