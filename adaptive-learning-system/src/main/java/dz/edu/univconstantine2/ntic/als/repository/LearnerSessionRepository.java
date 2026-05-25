package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.LearnerSession;
import dz.edu.univconstantine2.ntic.als.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Learner Session entities.
 */
@Repository
public interface LearnerSessionRepository extends JpaRepository<LearnerSession, String> {

    



    Optional<LearnerSession> findByEnrollmentIdAndModuleIdAndStatus(
            String enrollmentId,
            String moduleId,
            SessionStatus status);

    




    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE learner_sessions
            SET status = 'COMPLETED', updated_at = NOW()
            WHERE enrollment_id IN (
                SELECT e.id FROM enrollments e WHERE e.course_id = :courseId
            )
            AND status = 'IN_PROGRESS'
            """, nativeQuery = true)
    void abandonSessionsByCourseId(@Param("courseId") String courseId);
}
