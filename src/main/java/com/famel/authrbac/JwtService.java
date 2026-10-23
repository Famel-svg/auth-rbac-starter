package com.famel.authrbac;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = props.expirationSeconds();
    }

    public String issue(String email, AppUser.Role role) {
        Date now = new Date();
        return Jwts.builder().subject(email).claim("role", role.name())
                .issuedAt(now).expiration(new Date(now.getTime() + expirationSeconds * 1000))
                .signWith(key).compact();
    }

    /** Valida assinatura e expiracao; lanca JwtException se invalido. */
    public Claims parse(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
