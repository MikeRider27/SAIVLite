package com.saiv.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationHours;

    public JwtService(JwtProperties props) {
        byte[] bytes = props.secret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationHours = props.expirationHours();
    }

    public String generar(UsuarioPrincipal u) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(u.username())
                .claim("uid", u.id())
                .claim("rol", u.rol())
                .claim("perms", u.permisos())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public UsuarioPrincipal validar(String token) {
        Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new UsuarioPrincipal(
                ((Number) c.get("uid")).longValue(),
                c.getSubject(),
                c.get("rol", String.class),
                (List<String>) c.get("perms", List.class));
    }
}
