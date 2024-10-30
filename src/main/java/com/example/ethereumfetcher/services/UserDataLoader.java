package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.models.enums.Role;
import com.example.ethereumfetcher.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }

    private void loadUserData() {
        if (userRepository.findByUsername("alice").isEmpty()) {
            User alice = new User();
            alice.setUsername("alice");
            alice.setFirstName("Alice");
            alice.setLastName("Smith");
            alice.setPassword(passwordEncoder.encode("alice"));  // Паролата се криптира
            alice.setRole(Role.USER);  // Ролята на потребителя
            userRepository.save(alice);
        }

        if (userRepository.findByUsername("bob").isEmpty()) {
            User bob = new User();
            bob.setUsername("bob");
            bob.setFirstName("Bob");
            bob.setLastName("Brown");
            bob.setPassword(passwordEncoder.encode("bob"));
            bob.setRole(Role.USER);
            userRepository.save(bob);
        }

        if (userRepository.findByUsername("carol").isEmpty()) {
            User carol = new User();
            carol.setUsername("carol");
            carol.setFirstName("Carol");
            carol.setLastName("White");
            carol.setPassword(passwordEncoder.encode("carol"));
            carol.setRole(Role.USER);
            userRepository.save(carol);
        }

        if (userRepository.findByUsername("dave").isEmpty()) {
            User dave = new User();
            dave.setUsername("dave");
            dave.setFirstName("Dave");
            dave.setLastName("Black");
            dave.setPassword(passwordEncoder.encode("dave"));
            dave.setRole(Role.USER);
            userRepository.save(dave);
        }
    }
}