package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.AuthenticationResponse;
import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.models.enums.Role;
import com.example.ethereumfetcher.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateSuccessfully() {
        User request = new User();
        request.setUsername("johndoe");
        request.setPassword("password");

        User foundUser = new User();
        foundUser.setUsername("johndoe");
        foundUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(foundUser));
        when(jwtService.generateToken(foundUser)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void authenticateUserNotFound() {
        User request = new User();
        request.setUsername("johndoe");
        request.setPassword("password");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticateInvalidPassword() {
        User request = new User();
        request.setUsername("johndoe");
        request.setPassword("wrongPassword");

        User foundUser = new User();
        foundUser.setUsername("johndoe");
        foundUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(foundUser));
        doThrow(new RuntimeException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
    }
}