package com.raguileoam.virtualticket.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.io.IOException;
import java.security.Key;
import java.util.Collection;

public interface JWTService {

    Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    long TOKEN_DURATION = 3600 * 1000; // one hour
    String TOKEN_PREFIX = "Bearer ";
    String HEADER = "Authorization";
    String CLAIMS = "authorities";

    String createToken(Authentication authentication) throws IOException;

    boolean validateToken(String token);

    Claims getClaims(String token) throws UnsupportedJwtException;

    String getNombreFromToken(String token);

    Collection<? extends GrantedAuthority> getRolesFromToken(String token) throws IOException;

    String formatToken(String token);
}
