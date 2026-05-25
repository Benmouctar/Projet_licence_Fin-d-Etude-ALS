package dz.edu.univconstantine2.ntic.als.service.ai.legacy;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;






/**
 * Service class containing business logic, validation rules, and transactional processing for Document Indexing.
 */
@Deprecated(since = "2.0", forRemoval = false)
@ConditionalOnProperty(name = "app.ai.rag-pipeline", havingValue = "legacy", matchIfMissing = false)
@Service
@RequiredArgsConstructor
public class DocumentIndexingService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIndexingService.class);

    private final DocumentTextExtractor textExtractor;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;

    @Async
    public void indexModuleDocument(String moduleId, String courseId, String filePath) {
        log.info("Starting async indexing for moduleId: {}, courseId: {}, file: {}", moduleId, courseId, filePath);
        
        try {
            
            List<String> chunks = textExtractor.extractAndChunk(Path.of(filePath));
            if (chunks.isEmpty()) {
                log.warn("No chunks extracted for module {}. Indexing aborted.", moduleId);
                return;
            }
            log.info("Extracted {} chunks for module {}", chunks.size(), moduleId);

            
            log.info("Generating embeddings for {} chunks...", chunks.size());
            List<List<Float>> embeddings = embeddingService.embedBatch(chunks);
            log.info("Generated embeddings successfully.");

            
            log.info("Upserting to Qdrant...");
            qdrantService.upsertDocumentChunks(moduleId, courseId, chunks, embeddings);
            log.info("Finished indexing for module {}", moduleId);

        } catch (Exception e) {
            log.error("Failed to index document for module {}: {}", moduleId, e.getMessage());
        }
    }

    public void reindexModule(String moduleId, String courseId, String filePath) {
        log.info("Re-indexing module {}. Deleting old vectors first.", moduleId);
        qdrantService.deleteModuleVectors(moduleId);
        indexModuleDocument(moduleId, courseId, filePath);
    }
}
