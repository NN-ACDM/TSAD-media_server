package tsad.media.provider.utils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;

@Component
public class JwtUtils {

    @Value("${security.jwt-token.secret}")
    private String JWT_SECRET;
//    private final long expirationMs = 86400000; // 1 day

    private Key key;

    @PostConstruct
    public void init() throws IOException {
        key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
