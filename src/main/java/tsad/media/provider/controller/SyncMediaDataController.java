package tsad.media.provider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsad.media.provider.controller.models.SyncMediaDataRq;
import tsad.media.provider.services.SyncMediaDataService;

import java.io.File;

@RestController
@RequestMapping("/api/sync/media")
public class SyncMediaDataController {
    private final Logger log = LoggerFactory.getLogger(SyncMediaDataController.class);

    @Value("${spring.application.source-path.image}")
    private String IMAGE_BASE_PATH;

    @Value("${spring.application.source-path.audio}")
    private String AUDIO_BASE_PATH;

    @Value("${spring.application.source-path.video}")
    private String VIDEO_BASE_PATH;

    @Value("${spring.application.source-path.document}")
    private String DOCUMENT_BASE_PATH;

    @Autowired
    private SyncMediaDataService syncMediaDataService;

    @PostMapping("/image")
    public ResponseEntity<String> syncImageDetail(@RequestBody SyncMediaDataRq rq) {
        try {
            File targetDir = new File(IMAGE_BASE_PATH + "/" + rq.getTopic());
            syncMediaDataService.scanDirectory(targetDir);
            return ResponseEntity.ok("");
        } catch (Exception ex) {
            log.error("syncImageDetail() ... error: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("syncImageDetail() ... error: " + ex.getMessage());
        }
    }

    @PostMapping("/audio")
    public ResponseEntity<String> syncAudioDetail(@RequestBody SyncMediaDataRq rq) {
        try {
            File targetDir = new File(AUDIO_BASE_PATH + "/" + rq.getTopic());
            syncMediaDataService.scanDirectory(targetDir);
            return ResponseEntity.ok("");
        } catch (Exception ex) {
            log.error("syncAudioDetail() ... error: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("syncAudioDetail() ... error: " + ex.getMessage());
        }
    }

    @PostMapping("/video")
    public ResponseEntity<String> syncVideoDetail(@RequestBody SyncMediaDataRq rq) {
        try {
            File targetDir = new File(VIDEO_BASE_PATH + "/" + rq.getTopic());
            syncMediaDataService.scanDirectory(targetDir);
            return ResponseEntity.ok("");
        } catch (Exception ex) {
            log.error("syncVideoDetail() ... error: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("syncVideoDetail() ... error: " + ex.getMessage());
        }
    }

    @PostMapping("/document")
    public ResponseEntity<String> syncDocumentDetail(@RequestBody SyncMediaDataRq rq) {
        try {
            File targetDir = new File(DOCUMENT_BASE_PATH + "/" + rq.getTopic());
            syncMediaDataService.scanDirectory(targetDir);
            return ResponseEntity.ok("");
        } catch (Exception ex) {
            log.error("syncDocumentDetail() ... error: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("syncDocumentDetail() ... error: " + ex.getMessage());
        }
    }
}
