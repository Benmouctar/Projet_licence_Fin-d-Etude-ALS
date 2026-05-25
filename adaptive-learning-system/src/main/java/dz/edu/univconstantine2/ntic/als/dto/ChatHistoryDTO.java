/**
 * Data Transfer Object (DTO) representing a structural representation of Chat History data.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import java.time.Instant;

public record ChatHistoryDTO(
        String userQuery,
        String aiResponse,
        boolean wasOutOfContext,
        Instant timestamp
) {
}
