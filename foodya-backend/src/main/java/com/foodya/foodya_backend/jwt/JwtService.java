package com.foodya.foodya_backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("{app.jwt.expiration-time}")
  private Long jwtExpirationTime;

  @Value("{app.jwt.refresh-expiration-time}")
  private Long refreshExpirationTime;

  // Get signing key
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Extract username from token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extract expiration date from token
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Extract specific claim from token
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claimsResolver.apply(claims);
  }

  // Extract all claims from token
  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // Check if token is expired
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Generate token with user details
  public String generateToken(UserDetails userDetails) {
    // Claims (payload)
    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // Generate token with extra claims
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // Generate refresh token
  public String generateRefreshToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // Create token
  public String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // Validate token
  public Boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } catch (Exception e) {
      logger.error(token + " is invalid: " + e.getMessage());
      return false;
    }
  }

  // Validate token structure
  public Boolean validateTokenStructure(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token: {}", ex.getMessage());
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty: {}", ex.getMessage());
    } catch (Exception ex) {
      logger.error("JWT token validation error: {}", ex.getMessage());
    }
    return false;
  }

  // Get token expiration time in milliseconds
  public Long getJwtExpirationTime() {
    return jwtExpirationTime;
  }

  // Get refresh token expiration time in milliseconds
  public Long getRefreshToken() {
    return refreshExpirationTime;
  }

  // isValid token
  public Boolean isTokenValid(String token) {
    return validateTokenStructure(token) && !isTokenExpired(token);
  }
}
