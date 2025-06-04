package tsad.media.provider.controller;

import tsad.media.provider.controller.models.AccessUrlRq;
import tsad.media.provider.controller.models.AccessUrlRs;
import tsad.media.provider.services.VideoStreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/videos")
public class VideoStreamingController {

    @Autowired
    private VideoStreamingService videoStreamingService;

    @PostMapping("/access-url")
    public AccessUrlRs getTemporaryAccessUrl(@RequestBody AccessUrlRq rq) {
        String registerToken = videoStreamingService.getRegisterToken(rq);
        return new AccessUrlRs(registerToken);
    }

    @GetMapping(value = "/stream/{videoToken}", produces = "video/mp4")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String videoToken,
            @RequestHeader HttpHeaders headers) throws IOException {
        UrlResource videoResource = videoStreamingService.getVideoUrlResource(videoToken);

        if (videoResource == null || !videoResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ResourceRegion region = videoStreamingService.getResourceRegion(headers, videoResource.contentLength(), videoResource);

        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }
}
