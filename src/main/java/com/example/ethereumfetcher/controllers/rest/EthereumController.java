package com.example.ethereumfetcher.controllers.rest;

import com.example.ethereumfetcher.models.EthereumTransaction;
import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.services.JwtService;
import com.example.ethereumfetcher.services.contracts.EthereumTransactionService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lime")
public class EthereumController {

    @Autowired
    private EthereumTransactionService ethereumTransactionService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/eth")
    public ResponseEntity<List<EthereumTransaction>> fetchTransactions(
            @RequestParam List<String> transactionHashes,
            @RequestHeader(value = "AUTH_TOKEN", required = false) String authToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        ResponseEntity<List<EthereumTransaction>> UNAUTHORIZED = getListResponseEntity(authToken, authorizationHeader);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        List<EthereumTransaction> transactions = ethereumTransactionService.fetchAndSaveTransactions(transactionHashes);
        return ResponseEntity.ok(transactions);
    }



    @GetMapping("/all")
    public ResponseEntity<List<EthereumTransaction>> getAllTransactions() {
        List<EthereumTransaction> allTransactions = ethereumTransactionService.getAllTransactions();
        return ResponseEntity.ok(allTransactions);
    }


    @GetMapping("/my")
    public ResponseEntity<List<EthereumTransaction>> getUserTransactions(
            @RequestHeader(value = "AUTH_TOKEN", required = false) String authToken,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        ResponseEntity<List<EthereumTransaction>> UNAUTHORIZED = getListResponseEntity(authToken, authorizationHeader);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        User currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                instanceof User ? (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal() : null;
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<EthereumTransaction> userTransactions = ethereumTransactionService.getTransactionsByUserId(currentUser.getId());
        return ResponseEntity.ok(userTransactions);
    }




    @Nullable
    private ResponseEntity<List<EthereumTransaction>> getListResponseEntity(String authToken, String authorizationHeader) {
        String token = authToken != null ? authToken : (authorizationHeader != null ?
                authorizationHeader.replace("Bearer ", "") : null);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        jwtService.validateToken(token);
        return null;
    }
}