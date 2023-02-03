package com.raguileoam.virtualticket.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class JWTServiceImpl implements JWTService {
  private static final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);

  /**
   * It creates the JWT token.
   * Crea el token JWT.
   *
   * @param authentication It represents the token for an authentication request.
   *                       Representa el token para una solicitud de autenticación
   * @return JWT token. Token JWT.
   */
  @Override
  public String createToken(Authentication authentication) throws IOException {
    String username = ((User) authentication.getPrincipal()).getUsername();
    Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
    Claims claims = Jwts.claims();
    claims.put(CLAIMS, new ObjectMapper().writeValueAsString(roles));
    return Jwts.builder().setClaims(claims).setSubject(username).signWith(SECRET_KEY).setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_DURATION * 8l)).compact();
  }

  /**
   * It validates the JWT token.
   * Valida el token JWT.
   *
   * @param token JWT token.
   *              Token JWT.
   */
  @Override
  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * It gets the claims of the JWT token
   * Obtiene los claims del token JWT.
   *
   * @param token JWT Token.
   *              Token JWT.
   * @return Token claims.
   */
  @Override
  public Claims getClaims(String token) {
    String formatToken = formatToken(token);
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(formatToken).getBody();
  }

  /**
   * It gets the username stored in the JWT token.
   * Obtiene el nombre de usuario almacenado en el token JWT.
   *
   * @param token JWT token.
   *              Token JWT.
   * @return Username. Nombre de usuario.
   */
  @Override
  public String getNombreFromToken(String token) {
    return getClaims(token).getSubject();
  }

  /**
   * It gets the user's roles stored in the JWT token.
   * Obtiene los roles de un usuario almacenados en el token JWT.
   *
   * @param token JWT token.
   *              Token JWT.
   * @return Roles. Roles.
   * @throws IOException
   */
  @Override
  public Collection<? extends GrantedAuthority> getRolesFromToken(String token) throws IOException {
    Object roles = getClaims(token).get(CLAIMS);
    return Arrays.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
        .readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
  }

  /**
   * It formats the JWT token.
   *
   * @param token JWT token.
   *              Token JWT.
   * @return Formated token. Token formateado.
   */
  @Override
  public String formatToken(String token) {
    if (token != null && token.startsWith(TOKEN_PREFIX))
      return token.replace(TOKEN_PREFIX, "");
    return null;
  }
}
