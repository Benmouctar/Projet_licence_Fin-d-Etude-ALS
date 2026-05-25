/**
 * Component managing functionality and helper operations for Question Response For Learner.
 */
package dz.edu.univconstantine2.ntic.als.dto;

import dz.edu.univconstantine2.ntic.als.model.DifficultyLevel;

import java.util.List;




public record QuestionResponseForLearner(
        String id,
        String statement,
        List<String> options,
        DifficultyLevel difficultyLevel
) {}
