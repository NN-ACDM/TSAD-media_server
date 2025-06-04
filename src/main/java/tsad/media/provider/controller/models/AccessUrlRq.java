package tsad.media.provider.controller.models;


import tsad.media.provider.common.FileCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class AccessUrlRq {
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant expiredDate;
    private FileCategory category;
    private String path;
    private String filename;
}
