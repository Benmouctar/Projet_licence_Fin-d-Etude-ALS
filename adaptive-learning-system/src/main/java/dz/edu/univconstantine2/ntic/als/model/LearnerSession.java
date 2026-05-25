package dz.edu.univconstantine2.ntic.als.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "learner_sessions", indexes = {
        @Index(name = "idx_ls_enrollment_module_status",
               columnList = "enrollment_id, module_id, status")
})
/**
 * JPA Entity model representing a persistent Learner Session record within the database schema.
 */
public class LearnerSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Enrollment enrollment;

    
    @Column(name = "module_id", nullable = false)
    private String moduleId;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "current_difficulty", nullable = false)
    private DifficultyLevel currentDifficulty;

    



    @Column(name = "answered_question_ids", length = 5000)
    @Builder.Default
    private String answeredQuestionIds = "";

    



    @Column(name = "answer_results", length = 5000)
    @Builder.Default
    private String answerResults = "";

    
    @Column(name = "current_question_id")
    private String currentQuestionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    
    @Column(name = "final_score")
    private Integer finalScore;
}
