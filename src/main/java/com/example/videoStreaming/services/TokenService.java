package com.example.videoStreaming.services;

import com.example.videoStreaming.services.models.CustomToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.videoStreaming.utils.AesTokenUtils.encrypt;

@Service
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(TokenService.class);

    @Value("${security.ip-token.secret}")
    private static String SECRET_KEY;

    private static final Map<String, CustomToken> tokenStore = new ConcurrentHashMap<>();

    public String registerToken(String clientIp, Path filePath, int tokenExpireDuration, TemporalUnit tokenExpireUnit) {
        String token = this.generateTokenByIp(clientIp);
        Instant expiry = Instant.now().plus(tokenExpireDuration, tokenExpireUnit);
        tokenStore.put(token, new CustomToken(filePath, expiry));
        return token;
    }

    private String generateTokenByIp(String ipAddress) {
        try {
            return encrypt(SECRET_KEY, ipAddress);
        } catch (Exception e) {
            log.error("generateTokenByIp() ... Error encrypting IP: {}", ipAddress, e);
            throw new RuntimeException("Error encrypting IP");
        }
    }

    public Path getPathByToken(String token) {
        CustomToken data = tokenStore.get(token);
        if (data == null || Instant.now().isAfter(data.expiry())) {
            tokenStore.remove(token);
            return null;
        }
        Path path = data.filePath();
        log.debug("getFilePathByToken() ... token: {}, path: {}", token, data.filePath().toUri());
        return path ;
    }
}
