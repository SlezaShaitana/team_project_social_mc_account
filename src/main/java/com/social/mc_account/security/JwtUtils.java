package com.social.mc_account.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;


    public String getId(String token) {
        return getClaimsFromToken(token).get("id", String.class);
    }

    public String getEmail(String token) {
        return getClaimsFromToken(token).get("email", String.class);
    }

    public List<String> getRoles(String token) {
        return getClaimsFromToken(token).get("roles", List.class);
    }

    private Claims getClaimsFromToken(String token) {
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(createSecretKey(secret))
                .build();

        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(cleanToken);
        Claims claims = claimsJws.getBody();

        log.info("Claims: {}", claims);

        return claims;
    }


    public static SecretKey createSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}