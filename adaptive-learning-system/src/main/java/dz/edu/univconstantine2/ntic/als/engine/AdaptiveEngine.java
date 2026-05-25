package dz.edu.univconstantine2.ntic.als.engine;

import dz.edu.univconstantine2.ntic.als.model.DifficultyLevel;
import dz.edu.univconstantine2.ntic.als.model.Question;
import dz.edu.univconstantine2.ntic.als.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;







/**
 * Core engine managing the adaptive learning algorithm, session progression, and content recommendation rules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdaptiveEngine {

    

    
    static final int MIN_QUESTIONS = 5;

    



    static final int WINDOW_SIZE = 3;

    
    public static final int MAX_QUESTIONS = 15;

    

    private final QuestionRepository questionRepository;

    

    






    public DifficultyLevel adjustDifficulty(DifficultyLevel current, boolean wasCorrect) {
        return switch (current) {
            case EASY   -> wasCorrect ? DifficultyLevel.MEDIUM : DifficultyLevel.EASY;
            case MEDIUM -> wasCorrect ? DifficultyLevel.HARD   : DifficultyLevel.EASY;
            case HARD   -> wasCorrect ? DifficultyLevel.HARD   : DifficultyLevel.MEDIUM;
        };
    }

    

    










    public Optional<Question> selectNextQuestion(String moduleId,
                                                 DifficultyLevel difficulty,
                                                 Set<String> excludedIds) {

        Optional<Question> chosen = pickOne(moduleId, difficulty, excludedIds);
        if (chosen.isPresent()) {
            return chosen;
        }

        
        DifficultyLevel fallback = oneLevelDown(difficulty);
        if (fallback != difficulty) {
            log.debug("No questions at {} for module {}, falling back to {}",
                    difficulty, moduleId, fallback);
            return pickOne(moduleId, fallback, excludedIds);
        }

        return Optional.empty();
    }

    

    







    public boolean hasReachedConfidenceThreshold(List<Boolean> answers) {
        int n = answers.size();

        if (n >= MAX_QUESTIONS) {
            return true;
        }

        if (n < MIN_QUESTIONS) {
            return false;
        }

        
        
        
        
        List<Boolean> window = answers.subList(n - WINDOW_SIZE, n);
        boolean allCorrect = window.stream().allMatch(Boolean::booleanValue);

        return allCorrect;
    }

    

    


    public int calculateFinalScore(List<Boolean> answers) {
        if (answers.isEmpty()) return 0;
        long correct = answers.stream().filter(Boolean::booleanValue).count();
        return (int) Math.round((correct * 100.0) / answers.size());
    }

    

    private Optional<Question> pickOne(String moduleId,
                                       DifficultyLevel difficulty,
                                       Set<String> excludedIds) {
        List<Question> candidates;
        if (excludedIds.isEmpty()) {
            candidates = questionRepository.findRandomQuestions(
                    moduleId, difficulty, PageRequest.of(0, 1));
        } else {
            candidates = questionRepository.findRandomQuestions(
                    moduleId, difficulty,
                    List.copyOf(excludedIds),
                    PageRequest.of(0, 1));
        }
        return candidates.stream().findFirst();
    }

    private DifficultyLevel oneLevelDown(DifficultyLevel d) {
        return switch (d) {
            case HARD   -> DifficultyLevel.MEDIUM;
            case MEDIUM -> DifficultyLevel.EASY;
            case EASY   -> DifficultyLevel.EASY;
        };
    }
}
