package com.example.videoStreaming.controller;

import com.example.videoStreaming.services.VideoStreamingService;
import com.example.videoStreaming.services.VideoTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/videos")
public class VideoStreamingController {

    private final String VIDEO_PATH = "src/main/resources/session/";

    @Autowired
    private VideoStreamingService videoStreamingService;

    @Autowired
    private VideoTokenService videoTokenService;

//    @GetMapping(value = "/{fileName}", produces = "video/mp4")
//    public ResponseEntity<ResourceRegion> streamVideo(
//            @PathVariable String fileName,
//            @RequestHeader HttpHeaders headers) throws IOException {
//
//        File file = new File(VIDEO_PATH + fileName);
//        UrlResource video = new UrlResource(file.toURI());
//
//        long contentLength = video.contentLength();
//        ResourceRegion region = getResourceRegion(video, headers, contentLength);
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(region);
//    }

//    @GetMapping(value = "/stream/{filename}", produces = "video/mp4")
//    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String filename,
//                                                      @RequestParam String token,
//                                                      @RequestHeader HttpHeaders headers,
//                                                      HttpServletResponse response) throws IOException {

    /// /        String path = videoTokenService.validateToken(token);
    /// /        if (path == null || !path.endsWith(filename)) {
    /// /            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
    /// /            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    /// /        }
//
//        File file = new File(VIDEO_PATH + filename);
//        UrlResource video = new UrlResource(file.toURI());
//
//        long contentLength = video.contentLength();
//        ResourceRegion region = videoStreamingService.getResourceRegion(video, headers, contentLength);
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(region);
//    }
//    @GetMapping(value = "/stream/{filename}", produces = "video/mp4")
//    public ResponseEntity<ResourceRegion> streamVideo(
//            @PathVariable String filename,
//            @RequestParam String token,
//            @RequestHeader HttpHeaders headers) throws IOException {
//
//        // Point to the correct directory
//        File videoFile = new File(VIDEO_PATH + filename);
//
//        if (!videoFile.exists()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        UrlResource videoResource = new UrlResource(videoFile.toURI());
//        long contentLength = videoResource.contentLength();
//
//        // Parse range header
//        ResourceRegion region;
//        if (!headers.getRange().isEmpty()) {
//            HttpRange httpRange = headers.getRange().get(0);
//            long start = httpRange.getRangeStart(contentLength);
//            long end = httpRange.getRangeEnd(contentLength);
//            long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1); // 1MB chunks
//            region = new ResourceRegion(videoResource, start, rangeLength);
//        } else {
//            long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
//            region = new ResourceRegion(videoResource, 0, rangeLength);
//        }
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(
//                        MediaTypeFactory.getMediaType(videoResource)
//                                .orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
//                .body(region);
//    }

    private final Map<String, UrlResource> cache = new ConcurrentHashMap<>();

    @GetMapping(value = "/stream/{filename}", produces = "video/mp4")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String filename,
            @RequestParam String token,
            @RequestHeader HttpHeaders headers) throws IOException {

        UrlResource videoResource = cache.computeIfAbsent(filename, name -> {
            try {
                return new UrlResource(Paths.get(VIDEO_PATH + name).toUri());
            } catch (MalformedURLException e) {
                return null;
            }
        });

        if (videoResource == null || !videoResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        long contentLength = videoResource.contentLength();

        // Range handling same as before
        ResourceRegion region;
        if (!headers.getRange().isEmpty()) {
            HttpRange httpRange = headers.getRange().get(0);
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
            region = new ResourceRegion(videoResource, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }


//    @GetMapping(value = "/stream/{filename}", produces = "video/mp4")
//    public ResponseEntity<ResourceRegion> streamVideo(
//            @PathVariable String filename,
//            @RequestParam String token,
//            @RequestHeader HttpHeaders headers) throws IOException {
//
//        Path videoPath = Paths.get(VIDEO_PATH + filename);
//        if (!Files.exists(videoPath)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        UrlResource videoResource = new UrlResource(videoPath.toUri());
//        long contentLength = videoResource.contentLength();
//
//        ResourceRegion region;
//        if (!headers.getRange().isEmpty()) {
//            HttpRange httpRange = headers.getRange().get(0);
//            long start = httpRange.getRangeStart(contentLength);
//            long end = httpRange.getRangeEnd(contentLength);
//            long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1); // 1MB chunks
//            region = new ResourceRegion(videoResource, start, rangeLength);
//        } else {
//            long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
//            region = new ResourceRegion(videoResource, 0, rangeLength);
//        }
//
//        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                .contentType(MediaTypeFactory.getMediaType(videoResource)
//                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
//                .body(region);
//    }


    @PostMapping("/url")
    public Map<String, String> getTempUrl(@RequestBody Map<String, String> req, HttpServletRequest http) {
        String fileName = req.get("fileName"); // e.g., "sample.mp4"
        String filePath = "src/main/resources/source/" + fileName; // relative path to your local folder

        String token = videoTokenService.generateToken(filePath);
        String tempVideoFilename = videoStreamingService.generateTemporaryVideo(fileName, 240);

        String url = http.getScheme() + "://" + http.getServerName() + ":" + http.getServerPort()
                + "/api/videos/stream/" + tempVideoFilename + "?token=" + token;

        return Map.of("url", url);
    }
}
