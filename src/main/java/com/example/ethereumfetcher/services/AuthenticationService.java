package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.AuthenticationResponse;
import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.repositories.UserRepository;
import com.example.ethereumfetcher.exceptions.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(User request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public User tryToGetUserFromToken(String authToken, String authorizationHeader) {
        if (authToken == null && authorizationHeader == null) {
            throw new AuthenticationException("No token provided");
        }

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else if (authToken != null && authToken.startsWith("AUTH_TOKEN ")) {
            token = authToken.substring(11);
        }

        if (token == null) {
            throw new AuthenticationException("Invalid token");
        }

        String username = jwtService.extractUsername(token);

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
