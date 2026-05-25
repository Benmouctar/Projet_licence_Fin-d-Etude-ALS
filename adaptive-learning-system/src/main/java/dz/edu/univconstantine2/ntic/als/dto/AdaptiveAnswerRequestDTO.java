/**
 * Data Transfer Object (DTO) encapsulating input parameter payloads for requests to Adaptive Answer.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;




public record AdaptiveAnswerRequestDTO(
        @NotBlank String questionId,
        @NotNull Integer selectedAnswer
) {}
