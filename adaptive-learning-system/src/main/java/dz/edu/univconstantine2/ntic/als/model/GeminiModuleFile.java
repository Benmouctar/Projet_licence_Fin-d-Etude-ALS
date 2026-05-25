package dz.edu.univconstantine2.ntic.als.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;









/**
 * JPA Entity model representing a persistent Gemini Module File record within the database schema.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gemini_module_files")
public class GeminiModuleFile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    
    @Column(name = "module_id", nullable = false, unique = true)
    private String moduleId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    



    @Column(name = "google_file_uri", nullable = false, length = 500)
    private String googleFileUri;

    @Column(name = "display_name", length = 500)
    private String displayName;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    
    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    



    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
