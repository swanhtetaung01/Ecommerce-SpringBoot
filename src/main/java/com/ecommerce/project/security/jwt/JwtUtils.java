package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private static final Logger logger = LoggerFactory.getLogger("JwtUtils.class");

    public SecretKey key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey)); }

    public String generateJwtTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
           return bearerToken.substring(7);
        }
        return null;
    }

    public String generateJwtTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String generateUsernameFromJwtToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(key())
                .build().parseSignedClaims(jwtToken)
                .getPayload().getSubject();
    }

    public boolean validateJwtToken(String jwtToken) {
        try {
            System.out.println("Validating token");
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwtToken);
            System.out.println("Validated");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
