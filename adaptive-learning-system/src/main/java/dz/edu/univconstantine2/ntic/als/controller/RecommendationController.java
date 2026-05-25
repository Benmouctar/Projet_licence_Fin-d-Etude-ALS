package dz.edu.univconstantine2.ntic.als.controller;

import dz.edu.univconstantine2.ntic.als.dto.CourseResponseDTO;
import dz.edu.univconstantine2.ntic.als.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;






/**
 * REST controller exposing API endpoints for managing and retrieving Recommendation data.
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('LEARNER')")
public class RecommendationController {

    private final RecommendationService recommendationService;

    



    @GetMapping("/my")
    public ResponseEntity<List<CourseResponseDTO>> getMyRecommendations(
            @AuthenticationPrincipal UserDetails principal) {

        List<CourseResponseDTO> recommendations =
                recommendationService.getRecommendations(principal.getUsername());

        log.debug("[Recommendations] Returning {} recommendations for {}",
                recommendations.size(), principal.getUsername());

        return ResponseEntity.ok(recommendations);
    }
}
