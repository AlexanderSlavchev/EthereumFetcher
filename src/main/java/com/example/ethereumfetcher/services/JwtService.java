package com.example.ethereumfetcher.services;

import com.example.ethereumfetcher.models.User;
import com.example.ethereumfetcher.exceptions.InvalidTokenException;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

//    @Value("${jwt.privateKeyPath}")
//    private String privateKeyPath;
//
//    @Value("${jwt.publicKeyPath}")
//    private String publicKeyPath;

    private final String privateKeyPath;
    private final String publicKeyPath;

    public JwtService(Dotenv dotenv) {
        this.privateKeyPath = dotenv.get("JWT_PRIVATE_KEY_PATH");
        this.publicKeyPath = dotenv.get("JWT_PUBLIC_KEY_PATH");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public ResponseEntity<?> validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired.");
            }
            String username = claims.getSubject();
            return ResponseEntity.ok("Token is valid for user: " + username);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }
    }

    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getPrivateKey(), io.jsonwebtoken.SignatureAlgorithm.RS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PrivateKey getPrivateKey() {
        try {
            System.out.println("privateKeyPath: " + privateKeyPath);
            System.out.println("--------");
            System.out.println(Paths.get(privateKeyPath));
            String privateKeyPEM = new String(Files.readAllBytes(Paths.get(privateKeyPath)))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading private key", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            String publicKeyPEM = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading public key", e);
        }
    }
}