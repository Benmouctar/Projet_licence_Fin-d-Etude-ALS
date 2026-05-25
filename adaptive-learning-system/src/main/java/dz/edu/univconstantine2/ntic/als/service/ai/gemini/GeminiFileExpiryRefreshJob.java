package dz.edu.univconstantine2.ntic.als.service.ai.gemini;

import dz.edu.univconstantine2.ntic.als.repository.GeminiModuleFileRepository;
import dz.edu.univconstantine2.ntic.als.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;











/**
 * Component managing functionality and helper operations for Gemini File Expiry Refresh Job.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiFileExpiryRefreshJob {

    private final GeminiModuleFileRepository fileRepository;
    private final GeminiFileApiService fileApiService;
    private final FileStorageService fileStorageService;

    





    @Scheduled(cron = "0 0 */6 * * *")
    public void refreshExpiringSoonFiles() {
        Instant cutoff = Instant.now().plus(12, ChronoUnit.HOURS);
        var expiringSoon = fileRepository.findByExpiresAtBeforeAndActiveTrue(cutoff);

        if (expiringSoon.isEmpty()) {
            log.debug("[GeminiFileExpiry] No files expiring within 12h — nothing to refresh");
            return;
        }

        log.info("[GeminiFileExpiry] {} file(s) expiring within 12h — starting refresh", expiringSoon.size());

        expiringSoon.forEach(record -> {
            try {
                var path = fileStorageService.load(record.getDisplayName());
                fileApiService.uploadModuleFile(
                    record.getModuleId(), record.getCourseId(),
                    path, record.getMimeType()
                );
                log.info("[GeminiFileExpiry] Successfully refreshed file for module {}", record.getModuleId());
            } catch (Exception e) {
                log.error("[GeminiFileExpiry] Failed to refresh file for module {}: {}",
                    record.getModuleId(), e.getMessage());
            }
        });
    }
}
