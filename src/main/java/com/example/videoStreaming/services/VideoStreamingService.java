package com.example.videoStreaming.services;

import com.example.videoStreaming.common.TokenExpireUnit;
import com.example.videoStreaming.controller.models.VideoAccessUrlRq;
import com.example.videoStreaming.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableAsync
public class VideoStreamingService {
    private final Logger log = LoggerFactory.getLogger(VideoStreamingService.class);

    @Value("${spring.application.source.video.content-size:1}")
    private int VIDEO_CONTENT_SIZE;

    @Value("${spring.application.source-path.video}")
    private String VIDEO_SOURCE_DIR;

    @Value("${security.token.expire-duration.video}")
    private int TOKEN_EXPIRED_DURATION;

    @Value("${security.token.expire-unit.video}")
    private String TOKEN_EXPIRED_UNIT;

    private final Map<Path, UrlResource> cache = new ConcurrentHashMap<>();

//    @Value("${}")
//    private int EXPIRE_MINUTES;

//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FileUtils fileUtils;

    @PostConstruct
    public void init() throws IOException {
        if (Files.notExists(Paths.get(VIDEO_SOURCE_DIR))) {
            Files.createDirectories(Paths.get(VIDEO_SOURCE_DIR));
            log.info("init() ... create DIR: {}", VIDEO_SOURCE_DIR);
        }
    }

    public UrlResource getVideoUrlResource(String videoToken) {
        Path filePath = tokenService.getPathByToken(videoToken);
        return cache.computeIfAbsent(filePath,
                name -> {
                    try {
                        return new UrlResource(filePath.toUri());
                    } catch (MalformedURLException e) {
                        return null;
                    }
                });
    }

    public ResourceRegion getResourceRegion(HttpHeaders headers,
                                                   long contentLength,
                                                   UrlResource videoResource) {
        ResourceRegion region;
        if (!headers.getRange().isEmpty()) {
            HttpRange httpRange = headers.getRange().getFirst();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Math.min((long) VIDEO_CONTENT_SIZE * 1024 * 1024, end - start + 1);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            long rangeLength = Math.min((long) VIDEO_CONTENT_SIZE * 1024 * 1024, contentLength);
            region = new ResourceRegion(videoResource, 0, rangeLength);
        }

        return region;
    }

    public String getRegisterToken(String clientIp, VideoAccessUrlRq rq) {
        Path videoPath;
        try {
            videoPath = fileUtils.findFilePathByFileName(Paths.get(VIDEO_SOURCE_DIR), rq.getFileName());
        } catch (RuntimeException re) {
            log.error("getRegisterToken() ... finding error: {}", re.getMessage(), re);
            throw new RuntimeException("generate access url error via finding");
        }

        if (ObjectUtils.isEmpty(videoPath)) {
            log.warn("getRegisterToken() ... file: {} not found", rq.getFileName());
            throw new RuntimeException("generate access url error");
        }

        try {
            String registerToken = tokenService.registerToken(
                    "16810",
                    videoPath,
                    TOKEN_EXPIRED_DURATION,
                    TokenExpireUnit.fromString(TOKEN_EXPIRED_UNIT).getChronoUnit()
            );
            log.info("getRegisterToken() ... IP: {} is trying to access file: {} -> sending back token: {}", clientIp, videoPath.getFileName(), registerToken);
            return registerToken;
        } catch (RuntimeException re) {
            log.error("getRegisterToken() ... generating error: {}", re.getMessage(), re);
            throw new RuntimeException("generate access url error via generate token");
        }
    }

//    public String generateTemporaryVideo(Path sourcePath) {
//        if (!Files.exists(sourcePath)) {
//            log.warn("generateTemporaryVideo() ... Source file not found: {}", sourcePath);
//            throw new RuntimeException("Source file not found: " + sourcePath);
//        }
//
//        String randomName = String.valueOf(UUID.randomUUID());
//        Path targetPath = Paths.get(SESSION_DIR.toUri()).resolve(randomName);
//
//        try {
//            Files.createDirectories(targetPath.getParent());
//            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            log.error("generateTemporaryVideo() ... Failed to copy file: {}", sourcePath, e);
//            throw new RuntimeException("Failed to copy file", e);
//        }
//
//        this.scheduleDeletion(targetPath);
//        return randomName;
//    }

//    @Async
//    public void scheduleDeletion(Path path) {
//        scheduler.schedule(() -> {
//            try {
//                Files.deleteIfExists(path);
//                log.info("scheduleDeletion() ... File: {} will be deleted in {} {}",
//                        path, EXPIRE_MINUTES, TIME_UNIT.getClass().getName());
//            } catch (IOException e) {
//                log.error("scheduleDeletion() ... Error while delete: {}", path, e);
//            }
//        }, EXPIRE_MINUTES, TIME_UNIT);
//    }
}
