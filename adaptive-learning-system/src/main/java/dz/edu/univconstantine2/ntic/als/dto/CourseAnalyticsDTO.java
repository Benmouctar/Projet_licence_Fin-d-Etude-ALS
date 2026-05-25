/**
 * Data Transfer Object (DTO) representing a structural representation of Course Analytics data.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import java.util.List;





public record CourseAnalyticsDTO(
        String courseId,
        String courseTitle,
        long totalEnrollments,
        double overallAverageScore,
        List<ModuleAnalyticsDTO> modules
) {}
