package dz.edu.univconstantine2.ntic.als.repository;

import dz.edu.univconstantine2.ntic.als.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository interface defining database persistence and custom queries for Notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    



    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.readStatus = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
}
