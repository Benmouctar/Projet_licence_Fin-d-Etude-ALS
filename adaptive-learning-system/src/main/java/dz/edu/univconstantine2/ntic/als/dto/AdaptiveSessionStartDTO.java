/**
 * Data Transfer Object (DTO) representing a structural representation of Adaptive Session Start data.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import dz.edu.univconstantine2.ntic.als.model.DifficultyLevel;




public record AdaptiveSessionStartDTO(
        String sessionId,
        QuestionResponseForLearner firstQuestion,
        DifficultyLevel currentDifficulty,
        int questionsAnswered,
        int estimatedTotal
) {}
