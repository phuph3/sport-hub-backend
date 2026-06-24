package com.badminton.platform.service;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;



@Service
public class JwtService {

    private final Key key = Keys.hmacShaKeyFor(
            "my-super-secret-key-12345678901234567890".getBytes());

    public String generateToken(Long userId) {

        return Jwts.builder()
                .setSubject(userId.toString())
                // .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90)) // 90 ngày (3 tháng)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public Long getUserIdFromHeader(String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.replace("Bearer ", "");

            return extractUserId(token);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}