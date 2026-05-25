/**
 * Data Transfer Object (DTO) representing a structural representation of Admin Stats data.
 */
package dz.edu.univconstantine2.ntic.als.dto;







public record AdminStatsDTO(
        long totalUsers,
        long totalCourses,
        long totalEnrollments,
        long masteredEnrollments,
        long totalModules
) {}
