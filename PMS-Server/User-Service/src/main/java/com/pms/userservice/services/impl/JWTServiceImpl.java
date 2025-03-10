package com.pms.userservice.services.impl;

import com.pms.userservice.entities.User;
import com.pms.userservice.services.JWTService;
import com.pms.userservice.services.SessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    private final SessionService sessionService;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /* Generate Access token */
    public String generateAccessToken(User user ) {
        return Jwts.builder()
                .subject(user.getUserId())
                .claim("roles",user.getRole())
                .claim("email",user.getEmail())
                .claim("name",user.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24)) // Access Token will be valid for 24 hours
                .signWith(getSecretKey())
                .compact();

    }

    /* Generate Refresh Token */
    public String generateRefreshToken(User user ) {

        return Jwts.builder()
                .subject(user.getUserId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24*15)) // refresh Token will be valid for 15 days
                .signWith(getSecretKey())
                .compact();

    }

//    /* Validate the token and return the userId */
//    public String getUserIdFromToken(String bearerToken) {
//        Claims claims = Jwts.parser()
//                .verifyWith(getSecretKey())
//                .build()
//                .parseSignedClaims(bearerToken)
//                .getPayload();
//        return claims.getSubject();
//    }

}
