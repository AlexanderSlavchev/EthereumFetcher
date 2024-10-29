package com.example.ethereumfetcher.repositories;

import com.example.ethereumfetcher.models.EthereumTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<EthereumTransaction, String> {
    List<EthereumTransaction> findAll();
}