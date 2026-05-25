package dz.edu.univconstantine2.ntic.als.event;

import dz.edu.univconstantine2.ntic.als.service.ProactiveRemediationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Application event listener executing business actions when a Notification event is published.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final ProactiveRemediationService proactiveRemediationService;

    @Async
    @EventListener
    public void handleEnrollment(UserEnrolledEvent event) {
        log.info("User enrolled — event received: {}", event.enrollmentId());
        
    }

    @Async
    @EventListener
    public void handleRemediation(RemediationNeededEvent event) {
        log.info("Remediation needed — delegating to ProactiveRemediationService: enrollmentId={}",
                event.enrollmentId());
        proactiveRemediationService.handleRemediationNeeded(event);
    }
}
