package com.example.videoStreaming.services.models;

import java.nio.file.Path;
import java.time.Instant;

public record CustomToken(Path filePath, Instant expiry) {
}
