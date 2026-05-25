/**
 * Data Transfer Object (DTO) encapsulating input parameter payloads for requests to Adaptive Start.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import jakarta.validation.constraints.NotBlank;




public record AdaptiveStartRequestDTO(
        @NotBlank String enrollmentId,
        @NotBlank String moduleId
) {}
