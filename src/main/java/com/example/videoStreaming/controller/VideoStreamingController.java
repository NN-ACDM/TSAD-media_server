package com.example.videoStreaming.controller;

import com.example.videoStreaming.controller.models.VideoAccessUrlRq;
import com.example.videoStreaming.controller.models.VideoAccessUrlRs;
import com.example.videoStreaming.services.VideoStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/videos")
public class VideoStreamingController {
//    private final Logger log = LoggerFactory.getLogger(VideoStreamingController.class);

    @Autowired
    private VideoStreamingService videoStreamingService;

    @PostMapping("/access-url")
    public VideoAccessUrlRs getTemporaryAccessUrl(@RequestAttribute("clientIp") String clientIp,
                                                  @RequestBody VideoAccessUrlRq rq) {
        String registerToken = videoStreamingService.getRegisterToken(clientIp, rq);
        return new VideoAccessUrlRs(registerToken);
    }

    @GetMapping(value = "/stream/{videoToken}", produces = "video/mp4")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String videoToken,
            @RequestHeader HttpHeaders headers) throws IOException {
        UrlResource videoResource = videoStreamingService.getVideoUrlResource(videoToken);

        if (videoResource == null || !videoResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ResourceRegion region = VideoStreamingService.getResourceRegion(headers, videoResource.contentLength(), videoResource);

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }
}
