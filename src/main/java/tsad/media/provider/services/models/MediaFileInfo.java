package tsad.media.provider.services.models;

import lombok.Data;

@Data
public class MediaFileInfo {
    private String filename;
    private String extension;
    private long size;
    private Double durationSeconds;
    private String resourcePath;

    public MediaFileInfo(String filename, String extension, long size, Double durationSeconds, String resourcePath) {
        this.filename = filename;
        this.extension = extension;
        this.size = size;
        this.durationSeconds = durationSeconds;
        this.resourcePath = resourcePath;
    }
}
