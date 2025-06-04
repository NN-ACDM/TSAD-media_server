package tsad.media.provider.common;

import java.time.temporal.ChronoUnit;

public enum TokenExpireUnit {
    DAY(ChronoUnit.DAYS),
    HOUR(ChronoUnit.HOURS),
    MINUTE(ChronoUnit.MINUTES),
    SECOND(ChronoUnit.SECONDS);

    private final ChronoUnit unit;

    TokenExpireUnit(ChronoUnit unit) {
        this.unit = unit;
    }

    public ChronoUnit getChronoUnit() {
        return unit;
    }

    public static TokenExpireUnit fromString(String value) {
        return switch (value.toLowerCase()) {
            case "day" -> DAY;
            case "hour" -> HOUR;
            case "minute" -> MINUTE;
            case "second" -> SECOND;
            default -> throw new IllegalArgumentException("Unsupported unit: " + value);
        };
    }
}
