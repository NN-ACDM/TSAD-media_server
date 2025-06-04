package tsad.media.provider.controller.models;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SyncMediaDataRq {

    @NotNull(message = "Topic must not be null")
    private String topic;
}
