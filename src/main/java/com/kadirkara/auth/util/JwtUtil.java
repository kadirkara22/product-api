package com.kadirkara.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationTime = 1000 * 60 * 60 * 10; // 10 hours


    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    public Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(jwt).getPayload();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return extractAllClaims(jwt).getExpiration().before(new Date());
    }

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder().subject(username)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }
}
