
package com.rede.rede_restaurantes.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    public String gerarToken(UserDetails usuario) {

        long tempoAtual = System.currentTimeMillis();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(new Date(tempoAtual))
                .expiration(new Date(tempoAtual + 1000 * 60 * 60))
                .signWith(secretKey)
                .compact();
    }

    public String extrairEmail(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean tokenValido(String token, UserDetails usuario) {

        try {
            String email = extrairEmail(token);

            return email.equals(usuario.getUsername())
                    && !tokenExpirado(token);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean tokenExpirado(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().before(new Date());
    }
}
