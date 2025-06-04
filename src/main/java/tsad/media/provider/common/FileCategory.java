package tsad.media.provider.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FileCategory {
    VIDEO,
    IMAGE,
    DOCUMENT,
    AUDIO;

    @JsonCreator
    public static FileCategory fromString(String key) {
        return key == null ? null : FileCategory.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
}
