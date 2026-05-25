package dz.edu.univconstantine2.ntic.als.event;

import dz.edu.univconstantine2.ntic.als.service.FileStorageService;
import dz.edu.univconstantine2.ntic.als.service.ai.gemini.GeminiFileApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Application event listener executing business actions when a Gemini File Indexing event is published.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.ai.rag-pipeline", havingValue = "gemini", matchIfMissing = true)
public class GeminiFileIndexingListener {

    private final GeminiFileApiService geminiFileApiService;
    private final FileStorageService fileStorageService;

    @Async
    @EventListener
    public void onFileIndexingRequest(FileIndexingRequestEvent event) {
        log.info("[GeminiFileIndexingListener] Received FileIndexingRequestEvent for module: {}", event.moduleId());

        try {
            Path fullPath = fileStorageService.load(event.filePath());
            
            
            String mimeType = "application/pdf";
            String fileName = fullPath.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".mp4")) {
                mimeType = "video/mp4";
            } else if (fileName.endsWith(".txt")) {
                mimeType = "text/plain";
            }
            
            geminiFileApiService.uploadModuleFile(
                    event.moduleId(),
                    event.courseId(),
                    fullPath,
                    mimeType
            );
            log.info("[GeminiFileIndexingListener] Successfully uploaded file to Gemini for module: {}", event.moduleId());
        } catch (Exception e) {
            log.error("[GeminiFileIndexingListener] Error uploading file to Gemini: {}", e.getMessage(), e);
        }
    }
}
