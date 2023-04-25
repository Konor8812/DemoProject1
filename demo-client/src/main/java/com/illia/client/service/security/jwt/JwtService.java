package com.illia.client.service.security.jwt;

import com.illia.client.config.security.JwtSecretProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Autowired
  JwtSecretProvider jwtSecretProvider;

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String createToken(Map<String, Object> claims, UserDetails user) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .setIssuedAt(new Date(System.currentTimeMillis())) // there'll be issues with dates
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String createToken(UserDetails user) {
    return createToken(new HashMap<>(), user);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecretProvider.getEncodedSecret());
  }

  public boolean containsValidToken(String jwtHeader) {
    if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
      var token = jwtHeader.substring(7);
      try{
        return extractAllClaims(token).getExpiration()
            .before(new Date(System.currentTimeMillis())); // bad dates practice
      }catch (JwtException ex){
        return false;
      }
    }

    return false;
  }


}
