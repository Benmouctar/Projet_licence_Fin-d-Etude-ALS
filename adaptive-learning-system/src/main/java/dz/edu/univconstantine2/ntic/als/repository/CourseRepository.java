package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.Course;
import dz.edu.univconstantine2.ntic.als.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Course entities.
 */
public interface CourseRepository extends JpaRepository<Course, String> {

    




    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.id = :id AND c.instructor.email = :email")
    boolean existsByIdAndInstructorEmail(@Param("id") String id, @Param("email") String email);

    @Query("SELECT c FROM Course c WHERE c.id = :id")
    @EntityGraph(attributePaths = {"modules", "instructor"})
    Optional<Course> findByIdWithModulesAndInstructor(@Param("id") String id);

    @Query("SELECT c FROM Course c WHERE c.instructor = :instructor")
    @EntityGraph(attributePaths = {"modules", "instructor"})
    List<Course> findByInstructorWithModules(@Param("instructor") User instructor);

    @Query("SELECT c FROM Course c WHERE (c.deleted = FALSE OR c.deleted IS NULL)")
    @EntityGraph(attributePaths = {"instructor"})
    List<Course> findAllActive();
    
    
    List<Course> findByInstructor(User instructor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE courses SET instructor_id = NULL, updated_at = NOW() WHERE instructor_id = :instructorId", nativeQuery = true)
    void nullifyInstructor(@Param("instructorId") Long instructorId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE courses SET deleted = TRUE, updated_at = NOW() WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE modules SET deleted = TRUE, updated_at = NOW() WHERE course_id = :courseId AND (deleted = FALSE OR deleted IS NULL)", nativeQuery = true)
    void softDeleteModulesByCourseId(@Param("courseId") String courseId);
}
