package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.GeminiModuleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;







/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Gemini Module File entities.
 */
public interface GeminiModuleFileRepository extends JpaRepository<GeminiModuleFile, String> {

    



    Optional<GeminiModuleFile> findByModuleIdAndActiveTrue(String moduleId);

    




    List<GeminiModuleFile> findByExpiresAtBeforeAndActiveTrue(Instant cutoff);

    



    @Query("SELECT g FROM GeminiModuleFile g WHERE g.courseId = :courseId AND g.active = true")
    List<GeminiModuleFile> findActiveByCourseId(@Param("courseId") String courseId);
}
