package com.example.videoStreaming.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;


@RestController
@RequestMapping("/hls")
public class HLSDownloaderController {

    private final String HLS_PATH = "src/main/resources/source/";

    @GetMapping("/{fileName}.mp4")
    public ResponseEntity<Resource> getPlaylist(@PathVariable String fileName) throws MalformedURLException {
        File file = new File(HLS_PATH + fileName + ".mp4");
        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(resource);
    }
}
