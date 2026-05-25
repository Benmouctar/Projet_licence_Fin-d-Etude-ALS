/**
 * Data Transfer Object (DTO) wrapping structural response data returned for Adaptive Answer queries.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import dz.edu.univconstantine2.ntic.als.model.DifficultyLevel;






public record AdaptiveAnswerResponseDTO(
        QuestionResponseForLearner nextQuestion,
        DifficultyLevel newDifficulty,
        boolean sessionComplete,
        Integer finalScore,
        boolean lastAnswerCorrect,
        int questionsAnswered
) {}
