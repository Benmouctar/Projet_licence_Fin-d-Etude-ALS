package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Chat History entities.
 */
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {

    
    List<ChatHistory> findByModuleIdAndEnrollmentIdOrderByCreatedAtAsc(
            String moduleId, String enrollmentId);
}
