package com.social.mc_account.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;
    private JwtParserBuilder getJwtParserBuilder() {
        return Jwts.parser().setSigningKey(createSecretKey(secret));
    }

    public String getId(String token){
        return Jwts.parser().verifyWith(createSecretKey(secret)).build()
                .parseSignedClaims(token).getPayload().get("id", String.class);
    }

    public String getEmail(String token){
        return getJwtParserBuilder().build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public List<String> getRoles(String token) {
        return getJwtParserBuilder().build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }

    public static SecretKey createSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}