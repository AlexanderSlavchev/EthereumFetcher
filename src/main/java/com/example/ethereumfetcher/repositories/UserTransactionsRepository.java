package com.example.ethereumfetcher.repositories;

import com.example.ethereumfetcher.models.UserTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserTransactionsRepository extends JpaRepository<UserTransactions, Integer> {
    Optional<UserTransactions> findByUserIdAndTransactionHash(int userId, String transactionHash);

    List<UserTransactions> findByUserId(int userId);
}