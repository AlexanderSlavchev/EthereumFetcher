package com.example.ethereumfetcher.controllers.rest;

import com.example.ethereumfetcher.exceptions.InvalidTransactionException;
import com.example.ethereumfetcher.models.EthereumTransaction;
import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.services.AuthenticationService;
import com.example.ethereumfetcher.services.JwtService;
import com.example.ethereumfetcher.services.contracts.EthereumTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lime")
public class EthereumController {

    @Autowired
    private EthereumTransactionService ethereumTransactionService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/eth")
    public ResponseEntity<List<EthereumTransaction>> fetchTransactions(
            @RequestParam List<String> transactionHashes,
            @RequestHeader(value = "AUTH_HEADER", required = false) String authToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (transactionHashes.isEmpty()) {
            throw new InvalidTransactionException("Transaction hashes cannot be empty");
        }

        List<EthereumTransaction> transactions =
                ethereumTransactionService.fetchAndSaveTransactions(transactionHashes);
        return ResponseEntity.ok(transactions);
    }


    @GetMapping("/all")
    public ResponseEntity<List<EthereumTransaction>> getAllTransactions() {
        List<EthereumTransaction> allTransactions = ethereumTransactionService.getAllTransactions();
        return ResponseEntity.ok(allTransactions);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EthereumTransaction>> getUserTransactions(
            @RequestHeader(value = "AUTH_HEADER", required = false) String authToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        User currentUser = authenticationService.tryToGetUserFromToken(authToken, authorizationHeader);

        List<EthereumTransaction> userTransactions =
                ethereumTransactionService.getTransactionsByUserId(currentUser.getId());
        return ResponseEntity.ok(userTransactions);
    }
}