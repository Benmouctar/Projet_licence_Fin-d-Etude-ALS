/**
 * Data Transfer Object (DTO) representing a structural representation of Module Access data.
 */
package dz.edu.univconstantine2.ntic.als.dto;




public record ModuleAccessDTO(
        boolean canAccess,
        String reason
) {}
