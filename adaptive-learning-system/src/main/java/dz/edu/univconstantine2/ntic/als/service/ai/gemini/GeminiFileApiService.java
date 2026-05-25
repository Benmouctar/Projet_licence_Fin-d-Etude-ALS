package dz.edu.univconstantine2.ntic.als.service.ai.gemini;

import dz.edu.univconstantine2.ntic.als.model.GeminiModuleFile;
import dz.edu.univconstantine2.ntic.als.repository.GeminiModuleFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;













/**
 * Service class containing business logic, validation rules, and transactional processing for Gemini File Api.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiFileApiService {

    private static final String FILES_API_BASE     = "https://generativelanguage.googleapis.com/upload/v1beta/files";
    private static final String FILES_API_METADATA = "https://generativelanguage.googleapis.com/v1beta/files";

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private final GeminiModuleFileRepository fileRepository;

    












    @Transactional
    public GeminiModuleFile uploadModuleFile(String moduleId,
                                              String courseId,
                                              Path filePath,
                                              String mimeType) {
        
        Optional<GeminiModuleFile> existing = fileRepository.findByModuleIdAndActiveTrue(moduleId);
        if (existing.isPresent() && !existing.get().isExpired()) {
            log.info("[GeminiFileApi] Reusing cached file URI for module {}", moduleId);
            return existing.get();
        }

        
        existing.ifPresent(f -> {
            f.setActive(false);
            fileRepository.save(f);
        });

        if (!aiEnabled || apiKey.isBlank()) {
            throw new IllegalStateException("AI is disabled or Gemini API key is missing.");
        }

        log.info("[GeminiFileApi] Uploading file for module {} to Google Files API", moduleId);

        try {
            byte[] fileBytes = Files.readAllBytes(filePath);

            
            String boundary = "bound-als-" + System.currentTimeMillis();
            String metadataJson = "{\"file\":{\"display_name\":\"module-" + moduleId + "\"}}";

            
            String partHeader1 = "--" + boundary + "\r\n"
                    + "Content-Type: application/json; charset=UTF-8\r\n\r\n"
                    + metadataJson + "\r\n";
            
            String partHeader2 = "--" + boundary + "\r\n"
                    + "Content-Type: " + mimeType + "\r\n\r\n";
            String bodyEnd = "\r\n--" + boundary + "--";

            byte[] p1 = partHeader1.getBytes(StandardCharsets.UTF_8);
            byte[] p2 = partHeader2.getBytes(StandardCharsets.UTF_8);
            byte[] pe = bodyEnd.getBytes(StandardCharsets.UTF_8);

            byte[] fullBody = new byte[p1.length + p2.length + fileBytes.length + pe.length];
            System.arraycopy(p1,        0, fullBody, 0,                                    p1.length);
            System.arraycopy(p2,        0, fullBody, p1.length,                            p2.length);
            System.arraycopy(fileBytes, 0, fullBody, p1.length + p2.length,                fileBytes.length);
            System.arraycopy(pe,        0, fullBody, p1.length + p2.length + fileBytes.length, pe.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("multipart/related; boundary=" + boundary));
            headers.set("X-Goog-Upload-Protocol", "multipart");
            headers.setContentLength(fullBody.length);

            String url = FILES_API_BASE + "?key=" + apiKey;
            HttpEntity<byte[]> request = new HttpEntity<>(fullBody, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response == null || !response.containsKey("file")) {
                throw new RuntimeException("Gemini Files API returned unexpected response: " + response);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> fileObj = (Map<String, Object>) response.get("file");

            
            
            String fileUri = (String) fileObj.get("uri");
            if (fileUri == null || fileUri.isBlank()) {
                
                String name = (String) fileObj.get("name");
                fileUri = FILES_API_METADATA + "/" + name;
                log.warn("[GeminiFileApi] 'uri' field missing, constructed fallback URI: {}", fileUri);
            }

            String expiresStr = (String) fileObj.get("expirationTime");

            GeminiModuleFile record = GeminiModuleFile.builder()
                    .moduleId(moduleId)
                    .courseId(courseId)
                    .googleFileUri(fileUri)
                    .displayName("module-" + moduleId)
                    .mimeType(mimeType)
                    .sizeBytes(filePath.toFile().length())
                    .expiresAt(expiresStr != null ? Instant.parse(expiresStr) : null)
                    .active(true)
                    .build();

            GeminiModuleFile saved = fileRepository.save(record);
            log.info("[GeminiFileApi] Uploaded and saved fileUri={} for module {}", fileUri, moduleId);
            return saved;

        } catch (IOException e) {
            log.error("[GeminiFileApi] Failed to read file {} for module {}: {}", filePath, moduleId, e.getMessage());
            throw new RuntimeException("Failed to read file for Gemini upload: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("[GeminiFileApi] Upload failed for module {}: {}", moduleId, e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to Gemini Files API: " + e.getMessage(), e);
        }
    }

    







    @Transactional
    public void deleteModuleFile(String moduleId) {
        fileRepository.findByModuleIdAndActiveTrue(moduleId).ifPresent(record -> {
            try {
                String url = FILES_API_METADATA + "/" + record.getGoogleFileUri() + "?key=" + apiKey;
                restTemplate.delete(url);
                log.info("[GeminiFileApi] Deleted remote file {} for module {}", record.getGoogleFileUri(), moduleId);
            } catch (Exception e) {
                log.warn("[GeminiFileApi] Failed to delete remote file for module {}: {}", moduleId, e.getMessage());
            } finally {
                record.setActive(false);
                fileRepository.save(record);
            }
        });
    }
}
