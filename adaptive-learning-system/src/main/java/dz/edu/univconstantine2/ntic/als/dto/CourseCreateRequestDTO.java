/**
 * Data Transfer Object (DTO) encapsulating input parameter payloads for requests to Course Create.
 */
package dz.edu.univconstantine2.ntic.als.dto;

public record CourseCreateRequestDTO(
    String title,
    String category,
    String description,
    String gradient
) {}
