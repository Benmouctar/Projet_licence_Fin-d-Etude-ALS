package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.Module;
import dz.edu.univconstantine2.ntic.als.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Module entities.
 */
public interface ModuleRepository extends JpaRepository<Module, String> {
    List<Module> findByCourseOrderByDisplayOrderAsc(Course course);

    



    List<Module> findByCourseAndDeletedFalseOrderByDisplayOrderAsc(Course course);

    






    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId " +
           "AND (m.deleted IS NULL OR m.deleted = false) " +
           "ORDER BY m.displayOrder ASC NULLS LAST")
    List<Module> findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc(@Param("courseId") String courseId);
}
