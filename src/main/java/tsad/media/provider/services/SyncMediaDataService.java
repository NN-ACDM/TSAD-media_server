package tsad.media.provider.services;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsad.media.provider.repository.webservicedb.jpa.VideoDetailRepository;
import tsad.media.provider.repository.webservicedb.jpa.model.VideoDetailEntity;
import tsad.media.provider.services.models.MediaFileInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SyncMediaDataService {
    private final Logger log = LoggerFactory.getLogger(SyncMediaDataService.class);

    @Autowired
    private VideoDetailRepository videoDetailRepository;

    private final Tika tika = new Tika();

    @Transactional
    public void scanDirectory(File directory) {
        log.info("scanDirectory() ... start");
        List<MediaFileInfo> result = new ArrayList<>();
        this.scanRecursive(directory, result);

        List<VideoDetailEntity> videoDetails = new ArrayList<>();
        for (MediaFileInfo info : result) {
            VideoDetailEntity videoDetail = new VideoDetailEntity();
            videoDetail.setFilename(info.getFilename());
            videoDetail.setExtension(info.getExtension());
            videoDetail.setDuration(info.getDurationSeconds());
            // videoDetail.setUploadDatetime();
            videoDetail.setAvailable(true);
            videoDetail.setFileSize(info.getSize());
            videoDetail.setResourcePath(info.getResourcePath());
        }

        videoDetailRepository.deleteAll();
        log.info("scanDirectory() ... delete all data done");
        videoDetailRepository.saveAll(videoDetails);
        log.info("scanDirectory() ... save all new data done");
    }

    private void scanRecursive(File dir, List<MediaFileInfo> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanRecursive(file, result);
            } else {
                String name = file.getName();
                String extension = getExtension(name);
                long size = file.length();
                Double duration = null;
                String resourcePath = file.getPath();
                // file.getPath(); // Path relative to the working directory
                // file.toPath(); // java.nio.file.Path object

                try {
                    String mime = tika.detect(file);
                    if (mime.startsWith("video")) {
                        duration = this.getDuration(file);
                    }
                } catch (IOException e) {
                    log.error("scanRecursive() ... error: {}", e.getMessage(), e);
                }

                result.add(new MediaFileInfo(name, extension, size, duration, resourcePath));
            }
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1).toLowerCase();
    }

    private Double getDuration(File videoFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries",
                    "format=duration",
                    "-of",
                    "default=noprint_wrappers=1:nokey=1",
                    videoFile.getAbsolutePath());
            Process process = pb.start();
            process.waitFor(5, TimeUnit.SECONDS);

            String output = new String(process.getInputStream().readAllBytes()).trim();
            return Double.parseDouble(output);
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.error("getDuration() ... error: {}", e.getMessage(), e);
            return null;
        }
    }
}
