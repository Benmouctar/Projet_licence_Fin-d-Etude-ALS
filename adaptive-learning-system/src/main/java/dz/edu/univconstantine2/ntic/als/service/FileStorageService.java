package dz.edu.univconstantine2.ntic.als.service;

import jakarta.annotation.PostConstruct;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service class containing business logic, validation rules, and transactional processing for File Storage.
 */
@Slf4j
@Service
public class FileStorageService {

    



    private static final Tika TIKA = new Tika();

    



    private static final Map<String, String> ALLOWED_MIME_PREFIXES = Map.of(
            "pdf",  "application/pdf",
            "mp4",  "video/",
            "webm", "video/",
            "mov",  "video/",
            "avi",  "video/",
            "mkv",  "video/"
    );

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    










    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new SecurityException("File must have a name.");
        }

        
        if (originalFilename.contains("..") || originalFilename.contains("/")
                || originalFilename.contains("\\") || originalFilename.contains("\0")) {
            throw new SecurityException("Invalid filename detected.");
        }

        
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        List<String> allowed = List.of("pdf", "mp4", "webm", "mov", "avi", "mkv");
        if (!allowed.contains(extension)) {
            throw new SecurityException("File type not permitted: " + extension);
        }

        
        
        String detectedMime;
        try (InputStream peek = file.getInputStream()) {
            detectedMime = TIKA.detect(peek);
        }
        String expectedPrefix = ALLOWED_MIME_PREFIXES.get(extension);
        if (expectedPrefix != null && !detectedMime.startsWith(expectedPrefix)) {
            log.warn("[FileStorage] MIME mismatch: declared ext={}, detected={}", extension, detectedMime);
            throw new SecurityException(
                    "File content does not match its extension (detected: " + detectedMime + ").");
        }

        
        String safeFilename = UUID.randomUUID().toString() + "." + extension;
        Path target = uploadPath.resolve(safeFilename).normalize();

        
        if (!target.startsWith(uploadPath)) {
            throw new SecurityException("Path resolution escaped upload directory.");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        log.debug("[FileStorage] Stored {} ({}) as {}", originalFilename, detectedMime, safeFilename);
        return safeFilename;
    }

    


    public Path load(String filename) {
        return uploadPath.resolve(filename).normalize();
    }

    


    public Resource loadAsResource(String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }
}
