package dz.edu.univconstantine2.ntic.als.service.ai.gemini;

import dz.edu.univconstantine2.ntic.als.model.GeminiModuleFile;
import dz.edu.univconstantine2.ntic.als.repository.GeminiModuleFileRepository;
import dz.edu.univconstantine2.ntic.als.service.RAGResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import dz.edu.univconstantine2.ntic.als.repository.ModuleRepository;
import dz.edu.univconstantine2.ntic.als.service.FileStorageService;

import java.util.*;









/**
 * Service class containing business logic, validation rules, and transactional processing for Gemini Multimodal Rag.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiMultimodalRagService {

    private static final String GEMINI_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Value("${ai.gemini.multimodal-chat-model:gemini-2.0-flash}")
    private String multimodalModel;

    @Value("${ai.gemini.max-tokens:1000}")
    private int maxTokens;

    @Value("${ai.gemini.temperature:0.3}")
    private float temperature;

    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private final GeminiModuleFileRepository fileRepository;
    private final ModuleRepository moduleRepository;
    private final GeminiFileApiService geminiFileApiService;
    private final FileStorageService fileStorageService;

    













    @Transactional
    public String generateMultimodalResponse(String query,
                                              String moduleId,
                                              String courseName,
                                              String moduleTitle) {
        if (!aiEnabled || apiKey.isBlank()) {
            return "AI Tutoring is currently not configured. Please contact your administrator.";  
        }

        Optional<GeminiModuleFile> fileRecord = getOrLazyUploadFileRecord(moduleId);
        boolean hasValidFile = fileRecord.isPresent();

        String url = GEMINI_BASE + multimodalModel + ":generateContent?key=" + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String systemPrompt = buildSystemPrompt(courseName, moduleTitle, hasValidFile);
        Map<String, Object> systemInstruction = Map.of("parts", Map.of("text", systemPrompt));

        List<Map<String, Object>> parts = new ArrayList<>();

        if (hasValidFile) {
            String fileUri = fileRecord.get().getGoogleFileUri();
            String mimeType = fileRecord.get().getMimeType() != null
                    ? fileRecord.get().getMimeType()
                    : "application/pdf";
            log.info("[GeminiMultimodalRag] Querying with fileUri={} for module {}", fileUri, moduleId);
            parts.add(Map.of("file_data", Map.of("mime_type", mimeType, "file_uri", fileUri)));
        } else {
            log.warn("[GeminiMultimodalRag] No active file for module {} — using text-only mode", moduleId);
        }
        parts.add(Map.of("text", query));

        Map<String, Object> userContent = Map.of("role", "user", "parts", parts);

        Map<String, Object> body = new HashMap<>();
        body.put("system_instruction", systemInstruction);
        body.put("contents", List.of(userContent));
        body.put("generationConfig", Map.of("temperature", temperature, "maxOutputTokens", maxTokens));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("[GeminiMultimodalRag] API call failed: {}", e.getMessage());
            return "An error occurred while generating the AI response. Please try again.";
        }
    }

    









    @Transactional
    public RAGResult asRagResult(String moduleId) {
        boolean hasFile = getOrLazyUploadFileRecord(moduleId).isPresent();

        return new RAGResult(
            hasFile ? List.of("[Gemini multimodal — file context active]") : List.of(),
            !hasFile
        );
    }

    
    
    

    private Optional<GeminiModuleFile> getOrLazyUploadFileRecord(String moduleId) {
        Optional<GeminiModuleFile> fileRecord = fileRepository.findByModuleIdAndActiveTrue(moduleId);
        if (fileRecord.isEmpty() || fileRecord.get().isExpired()) {
            return attemptLazyUpload(moduleId);
        }
        return fileRecord;
    }

    private Optional<GeminiModuleFile> attemptLazyUpload(String moduleId) {
        return moduleRepository.findById(moduleId).flatMap(module -> {
            String contentUrl = module.getContentUrl();
            if (contentUrl != null) {
                String filename = contentUrl;
                if (contentUrl.startsWith("/api/files/")) {
                    filename = contentUrl.substring("/api/files/".length());
                } else if (contentUrl.contains("/")) {
                    filename = contentUrl.substring(contentUrl.lastIndexOf("/") + 1);
                }
                try {
                    java.nio.file.Path path = fileStorageService.load(filename);
                    if (java.nio.file.Files.exists(path)) {
                        String mimeType = "application/pdf";
                        if (filename.toLowerCase().endsWith(".mp4")) mimeType = "video/mp4";
                        else if (filename.toLowerCase().endsWith(".txt")) mimeType = "text/plain";
                        
                        log.info("[GeminiMultimodalRag] Lazy uploading missing file to Gemini: {}", filename);
                        return Optional.of(geminiFileApiService.uploadModuleFile(moduleId, module.getCourse().getId(), path, mimeType));
                    }
                } catch (Exception e) {
                    log.error("[GeminiMultimodalRag] Failed lazy upload for module {}: {}", moduleId, e.getMessage());
                }
            }
            return Optional.empty();
        });
    }

    private String buildSystemPrompt(String courseName, String moduleTitle, boolean hasFile) {
        if (hasFile) {
            return String.format(
                "You are a Socratic AI Tutor for the course '%s', module '%s'. " +
                "Answer ONLY based on the provided course document. " +
                "Guide students with hints rather than direct answers. " +
                "Keep responses under 300 words and use Markdown formatting.",
                courseName, moduleTitle);
        } else {
            return String.format(
                "You are a helpful AI Tutor for the course '%s', module '%s'. " +
                "No course document has been uploaded yet, so answer from your general knowledge of the topic. " +
                "Guide students with hints rather than direct answers. " +
                "Keep responses under 300 words and use Markdown formatting.",
                courseName, moduleTitle);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        if (response == null) return "No response from AI.";
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "No response generated.";
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "Empty response from AI.";
        return (String) parts.get(0).get("text");
    }
}
