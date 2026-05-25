package dz.edu.univconstantine2.ntic.als.controller;

import dz.edu.univconstantine2.ntic.als.dto.NotificationDTO;
import dz.edu.univconstantine2.ntic.als.model.Notification;
import dz.edu.univconstantine2.ntic.als.model.User;
import dz.edu.univconstantine2.ntic.als.repository.NotificationRepository;
import dz.edu.univconstantine2.ntic.als.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;







/**
 * REST controller exposing API endpoints for managing and retrieving Notification data.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('LEARNER')")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    
    @GetMapping("/my")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(
            @AuthenticationPrincipal UserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<NotificationDTO> dtos = notificationRepository
                .findUnreadByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails principal) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notification not found: " + id));

        
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!notification.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        notification.setReadStatus(true);
        notificationRepository.save(notification);
        log.debug("[Notifications] Marked {} as read for user {}", id, user.getEmail());

        return ResponseEntity.noContent().build();
    }

    

    private NotificationDTO toDto(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getEnrollmentId(),
                n.getModuleId(),
                n.getModuleTitle(),
                n.getContent(),
                n.getType(),
                n.isReadStatus(),
                n.getCreatedAt()
        );
    }
}
