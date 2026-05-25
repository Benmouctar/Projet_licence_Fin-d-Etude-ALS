/**
 * Data Transfer Object (DTO) representing a structural representation of Module Analytics data.
 */
package dz.edu.univconstantine2.ntic.als.dto;






public record ModuleAnalyticsDTO(
        String moduleId,
        String moduleTitle,
        long totalEnrollments,
        double averageScore,
        long failCount
) {}
