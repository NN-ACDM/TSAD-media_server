package tsad.media.provider.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
public class FileUtils {
    private final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public Path findFilePathByFileName(Path targetPath, String fileName) {
        try (Stream<Path> paths = Files.walk(targetPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            log.error("findFilePathByName() ... error: {}", e.getMessage(), e);
            return null;
        }
    }
}
