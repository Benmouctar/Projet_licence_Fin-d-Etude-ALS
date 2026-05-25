package dz.edu.univconstantine2.ntic.als.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity model representing a persistent Message record within the database schema.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "module_id", nullable = false)
    private String moduleId;

    @Column(name = "enrollment_id", nullable = false)
    private String enrollmentId;

    @Column(name = "user_query", columnDefinition = "TEXT", nullable = false)
    private String userQuery;

    @Column(name = "ai_response", columnDefinition = "TEXT", nullable = false)
    private String aiResponse;

    @Column(name = "was_out_of_context", nullable = false)
    private boolean wasOutOfContext;

    @Column(name = "retrieved_chunk_count", nullable = false)
    private int retrievedChunkCount;

    @Column(name = "processing_time_ms", nullable = false)
    private long processingTimeMs;

    



    @Column(name = "gemini_file_uri", length = 500)
    private String geminiFileUri;

    




    @Column(name = "rag_pipeline_used", nullable = false, length = 20)
    @Builder.Default
    private String ragPipelineUsed = "legacy";
}
