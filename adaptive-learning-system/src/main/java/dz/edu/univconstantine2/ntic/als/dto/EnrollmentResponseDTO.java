/**
 * Data Transfer Object (DTO) wrapping structural response data returned for Enrollment queries.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import java.util.List;

public record EnrollmentResponseDTO(String id, String courseId, String courseTitle,
    String courseCategory, String courseGradient, List<ModuleResponseDTO> courseModules,
    String completedModuleIds, Integer score, String masteryState) {}
