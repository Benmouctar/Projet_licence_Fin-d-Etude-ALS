/**
 * Data Transfer Object (DTO) wrapping structural response data returned for User queries.
 */
package dz.edu.univconstantine2.ntic.als.dto;

public record UserResponseDTO(Long id, String email, String name,
    String initials, String role) {}
