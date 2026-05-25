/**
 * Data Transfer Object (DTO) wrapping structural response data returned for Chat queries.
 */
package dz.edu.univconstantine2.ntic.als.dto;

public record ChatResponseDTO(
        String response,
        boolean isOutOfContext,
        int retrievedChunkCount,
        long processingTimeMs
) {
}
