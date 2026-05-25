/**
 * Data Transfer Object (DTO) representing a structural representation of Notification data.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import dz.edu.univconstantine2.ntic.als.model.NotificationType;

import java.time.Instant;





public record NotificationDTO(
        String id,
        String enrollmentId,
        String moduleId,
        String moduleTitle,
        String content,
        NotificationType type,
        boolean readStatus,
        Instant createdAt
) {}
