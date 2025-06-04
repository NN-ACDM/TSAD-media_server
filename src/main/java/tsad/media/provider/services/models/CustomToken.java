package tsad.media.provider.services.models;

import java.nio.file.Path;
import java.time.Instant;

public record CustomToken(Path filePath, Instant expiry) {
}
