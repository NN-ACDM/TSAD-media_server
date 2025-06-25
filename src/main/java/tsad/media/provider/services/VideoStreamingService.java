package tsad.media.provider.services;

import tsad.media.provider.common.TokenExpireUnit;
import tsad.media.provider.controller.models.AccessUrlRq;
import tsad.media.provider.utils.FileUtils;
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

    public String getRegisterToken(AccessUrlRq rq) {
        Path videoPath;
        try {
            videoPath = fileUtils.findFilePathByFileName(Paths.get(VIDEO_SOURCE_DIR + "/" + rq.getPath()), rq.getFilename());
        } catch (RuntimeException re) {
            log.error("getRegisterToken() ... finding error: {}", re.getMessage(), re);
            throw new RuntimeException("generate access url error via finding");
        }

        if (ObjectUtils.isEmpty(videoPath)) {
            log.warn("getRegisterToken() ... file: {} not found", rq.getPath());
            throw new RuntimeException("generate access url error");
        }

        try {
            String registerToken = tokenService.registerToken(
                    rq.getUsername(),
                    videoPath,
                    TOKEN_EXPIRED_DURATION,
                    TokenExpireUnit.fromString(TOKEN_EXPIRED_UNIT).getChronoUnit());
            log.info("getRegisterToken() ... {} is trying to access file: {} -> sending back token: {}",
                    rq.getUsername(), videoPath.getFileName(), registerToken);
            return registerToken;
        } catch (RuntimeException re) {
            log.error("getRegisterToken() ... generating error: {}", re.getMessage(), re);
            throw new RuntimeException("generate access url error via generate token");
        }
    }
}
