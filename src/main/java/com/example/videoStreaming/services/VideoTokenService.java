package com.example.videoStreaming.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VideoTokenService {
    private final Map<String, TemporaryVideoToken> tokenStore = new ConcurrentHashMap<>();

    public String generateToken(String filePath) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(30, ChronoUnit.SECONDS);
        tokenStore.put(token, new TemporaryVideoToken(filePath, expiry));
        return token;
    }

    public String validateToken(String token) {
        TemporaryVideoToken data = tokenStore.get(token);
        if (data == null || Instant.now().isAfter(data.expiry())) {
            tokenStore.remove(token);
            return null;
        }
        return data.filePath();
    }

    public record TemporaryVideoToken(String filePath, Instant expiry) {
    }
}
