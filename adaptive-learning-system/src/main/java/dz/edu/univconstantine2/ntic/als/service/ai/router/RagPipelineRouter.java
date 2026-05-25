package dz.edu.univconstantine2.ntic.als.service.ai.router;

import dz.edu.univconstantine2.ntic.als.service.LLMService;
import dz.edu.univconstantine2.ntic.als.service.RAGResult;
import dz.edu.univconstantine2.ntic.als.service.ai.gemini.GeminiMultimodalRagService;
import dz.edu.univconstantine2.ntic.als.service.ai.legacy.RAGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

















/**
 * Component managing functionality and helper operations for Rag Pipeline Router.
 */
@Slf4j
@Service
public class RagPipelineRouter {

    @Value("${app.ai.rag-pipeline:gemini}")
    private String activePipeline;

    




    private final Optional<RAGService> legacyRagService;
    private final GeminiMultimodalRagService geminiRagService;

    @Autowired
    public RagPipelineRouter(Optional<RAGService> legacyRagService,
                              GeminiMultimodalRagService geminiRagService) {
        this.legacyRagService = legacyRagService;
        this.geminiRagService = geminiRagService;
    }

    











    public RAGResult retrieveContext(String query, String moduleId) {
        if ("gemini".equalsIgnoreCase(activePipeline)) {
            log.debug("[RagRouter] Using Gemini Multimodal pipeline for module {}", moduleId);
            return geminiRagService.asRagResult(moduleId);
        }
        log.debug("[RagRouter] Using Legacy Qdrant pipeline for module {}", moduleId);
        return legacyRagService
                .map(svc -> svc.retrieveContext(query, moduleId))
                .orElseGet(() -> {
                    log.error("[RagRouter] Legacy pipeline selected but RAGService bean is missing!");
                    return new RAGResult(List.of(), true);
                });
    }

    













    public String generateResponse(String query,
                                   String moduleId,
                                   String courseName,
                                   String moduleTitle,
                                   List<String> legacyChunks,
                                   LLMService legacyLlm) {
        if ("gemini".equalsIgnoreCase(activePipeline)) {
            return geminiRagService.generateMultimodalResponse(query, moduleId, courseName, moduleTitle);
        }
        return legacyLlm.generateResponse(query, courseName, moduleTitle, legacyChunks);
    }

    




    public boolean isGeminiActive() {
        return "gemini".equalsIgnoreCase(activePipeline);
    }
}
