package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Message entities.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    
    List<Message> findByModuleIdAndEnrollmentIdOrderByCreatedAtAsc(String moduleId, String enrollmentId);

    








    @Query(value = """
            SELECT * FROM messages
            WHERE enrollment_id IN (:enrollmentIds)
            ORDER BY created_at DESC
            """, nativeQuery = true)
    List<Message> findRecentByEnrollmentIds(@Param("enrollmentIds") Set<String> enrollmentIds,
                                            Pageable pageable);
}
