package com.ryotube.application.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private String privateKey = "123%&*@1";
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256,privateKey)
                .compact();
    }
    public String extractEmail(String token){
        return getClaimsFromToken(token).getSubject();
    }
    public boolean validateToken(String token,UserDetails userDetails){
        final String username = extractEmail(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
    public Date expirationDate(String token){
        return getClaimsFromToken(token)
                .getExpiration();
    }
    public boolean isTokenExpired(String token){
        return expirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    private Claims getClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(privateKey)
                .parseClaimsJws(token).getBody();
    }
}
