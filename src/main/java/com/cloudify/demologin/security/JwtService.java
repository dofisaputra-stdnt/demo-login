package com.cloudify.demologin.security;


import com.cloudify.demologin.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private Claims claims;

    public String generateToken(String username, String tenant) {
        tenant = tenant.trim().toLowerCase().replaceAll("\\s+", "_");
        return Jwts.builder()
                .subject(username)
                .audience().add(tenant).and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration * 60 * 1000))
                .signWith(getSignInKey())
                .compact();
    }

    public void validateToken(String token) throws JwtException {
        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername() {
        return claims.getSubject();
    }

    public String extractTenant() {
        return claims.getAudience().iterator().next();
    }
}
