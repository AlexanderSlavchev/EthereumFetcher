package com.example.ethereumfetcher.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Configuration
public class EnvConfig {

    private final Dotenv dotenv = Dotenv.configure().load();

    @PostConstruct
    public void setSystemProperties() {
        System.setProperty("DATABASE_URL", Objects.requireNonNull(dotenv.get("DATABASE_URL")));
        System.setProperty("DATABASE_USERNAME", Objects.requireNonNull(dotenv.get("DATABASE_USERNAME")));
        System.setProperty("DATABASE_PASSWORD", Objects.requireNonNull(dotenv.get("DATABASE_PASSWORD")));
        System.setProperty("ETHEREUM_NODE_URL", Objects.requireNonNull(dotenv.get("ETHEREUM_NODE_URL")));
        System.setProperty("JWT_PRIVATE_KEY_PATH", Objects.requireNonNull(dotenv.get("JWT_PRIVATE_KEY_PATH")));
        System.setProperty("JWT_PUBLIC_KEY_PATH", Objects.requireNonNull(dotenv.get("JWT_PUBLIC_KEY_PATH")));
    }

    @Bean
    public Dotenv dotenv() {
        return dotenv;
    }
}