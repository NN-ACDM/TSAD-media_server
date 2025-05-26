package com.example.videoStreaming.services;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public class VideoStreamingService {

    private static final Path SOURCE_DIR = Paths.get("src/main/resources/source");
    private static final Path SESSION_DIR = Paths.get("src/main/resources/session");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() throws IOException {
        if (Files.notExists(SESSION_DIR)) {
            Files.createDirectories(SESSION_DIR);
        }
    }

    public String generateTemporaryVideo(String filename, int expire) {
        Path sourcePath = Paths.get("src/main/resources/source").resolve(filename);

        if (!Files.exists(sourcePath)) {
            throw new RuntimeException("Source file not found: " + sourcePath);
        }

        String randomName = UUID.randomUUID() + "-" + filename;
        Path targetPath = Paths.get("src/main/resources/session").resolve(randomName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file", e);
        }

        this.scheduleDeletion(targetPath, expire);
        return randomName;
    }

    @Async
    public void scheduleDeletion(Path path, int expireSeconds) {
        scheduler.schedule(() -> {
            try {
                Files.deleteIfExists(path);
                // Optionally log success
            } catch (IOException e) {
                // Optionally log error
                System.out.println("error while delete: " + path);
            }
        }, expireSeconds, TimeUnit.SECONDS);
    }

//    public ResourceRegion getResourceRegion(UrlResource video, HttpHeaders headers, long contentLength) {
//        long chunkSize = 1024 * 1024 * 50; // 5MB chunks (~1 minute depending on bit rate)
//        long rangeStart = 0;
//        long rangeEnd = Math.min(chunkSize, contentLength - 1);
//
//        if (!headers.getRange().isEmpty()) {
//            HttpRange range = headers.getRange().get(0);
//            rangeStart = range.getRangeStart(contentLength);
//            rangeEnd = Math.min(rangeStart + chunkSize, contentLength - 1);
//        }
//
//        return new ResourceRegion(video, rangeStart, rangeEnd - rangeStart + 1);
//    }
}
