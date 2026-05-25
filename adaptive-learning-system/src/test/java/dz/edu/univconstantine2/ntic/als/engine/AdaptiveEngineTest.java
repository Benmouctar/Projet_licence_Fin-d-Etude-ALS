package dz.edu.univconstantine2.ntic.als.engine;

import dz.edu.univconstantine2.ntic.als.model.DifficultyLevel;
import dz.edu.univconstantine2.ntic.als.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;









@ExtendWith(MockitoExtension.class)
@DisplayName("AdaptiveEngine unit tests")
class AdaptiveEngineTest {

    @Mock
    private QuestionRepository questionRepository;

    private AdaptiveEngine engine;

    @BeforeEach
    void setUp() {
        engine = new AdaptiveEngine(questionRepository);
    }

    

    @Test
    @DisplayName("EASY + correct → MEDIUM")
    void adjustDifficulty_easyCorrect_returnsMedium() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.EASY, true))
                .isEqualTo(DifficultyLevel.MEDIUM);
    }

    @Test
    @DisplayName("EASY + wrong → EASY (floor)")
    void adjustDifficulty_easyWrong_returnsEasy() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.EASY, false))
                .isEqualTo(DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("MEDIUM + correct → HARD")
    void adjustDifficulty_mediumCorrect_returnsHard() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.MEDIUM, true))
                .isEqualTo(DifficultyLevel.HARD);
    }

    @Test
    @DisplayName("MEDIUM + wrong → EASY")
    void adjustDifficulty_mediumWrong_returnsEasy() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.MEDIUM, false))
                .isEqualTo(DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("HARD + correct → HARD (ceiling)")
    void adjustDifficulty_hardCorrect_returnsHard() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.HARD, true))
                .isEqualTo(DifficultyLevel.HARD);
    }

    @Test
    @DisplayName("HARD + wrong → MEDIUM")
    void adjustDifficulty_hardWrong_returnsMedium() {
        assertThat(engine.adjustDifficulty(DifficultyLevel.HARD, false))
                .isEqualTo(DifficultyLevel.MEDIUM);
    }

    

    @Test
    @DisplayName("Fewer than MIN_QUESTIONS answered → false")
    void hasReachedConfidenceThreshold_fewerThanMin_returnsFalse() {
        List<Boolean> answers = List.of(true, true, true, true); 
        assertThat(engine.hasReachedConfidenceThreshold(answers)).isFalse();
    }

    @Test
    @DisplayName("MAX_QUESTIONS answered → true regardless of window")
    void hasReachedConfidenceThreshold_maxQuestions_returnsTrue() {
        List<Boolean> answers = Collections.nCopies(AdaptiveEngine.MAX_QUESTIONS, true);
        assertThat(engine.hasReachedConfidenceThreshold(answers)).isTrue();
    }

    @Test
    @DisplayName("Last WINDOW_SIZE answers all correct after MIN_QUESTIONS → true")
    void hasReachedConfidenceThreshold_lastWindowAllCorrect_returnsTrue() {
        
        List<Boolean> answers = List.of(false, false, true, true, true);
        assertThat(engine.hasReachedConfidenceThreshold(answers)).isTrue();
    }

    @Test
    @DisplayName("Last WINDOW_SIZE answers mixed after MIN_QUESTIONS → false")
    void hasReachedConfidenceThreshold_lastWindowMixed_returnsFalse() {
        
        List<Boolean> answers = List.of(true, true, false, true, false);
        assertThat(engine.hasReachedConfidenceThreshold(answers)).isFalse();
    }

    @Test
    @DisplayName("Last WINDOW_SIZE answers all wrong after MIN_QUESTIONS → true")
    void hasReachedConfidenceThreshold_lastWindowAllWrong_returnsTrue() {
        List<Boolean> answers = List.of(true, true, false, false, false);
        assertThat(engine.hasReachedConfidenceThreshold(answers)).isTrue();
    }

    

    @Test
    @DisplayName("All correct answers → 100%")
    void calculateFinalScore_allCorrect_returns100() {
        List<Boolean> answers = List.of(true, true, true, true, true);
        assertThat(engine.calculateFinalScore(answers)).isEqualTo(100);
    }

    @Test
    @DisplayName("All wrong answers → 0%")
    void calculateFinalScore_allWrong_returns0() {
        List<Boolean> answers = List.of(false, false, false, false);
        assertThat(engine.calculateFinalScore(answers)).isEqualTo(0);
    }

    @Test
    @DisplayName("Mixed answers → rounded percentage")
    void calculateFinalScore_mixed_returnsRoundedPercentage() {
        
        List<Boolean> answers = List.of(true, false, true);
        assertThat(engine.calculateFinalScore(answers)).isEqualTo(67);
    }
}
