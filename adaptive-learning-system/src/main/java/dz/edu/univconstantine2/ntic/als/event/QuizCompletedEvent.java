/**
 * Spring Application Event representing the occurrence of a Quiz Completed activity.
 */
package dz.edu.univconstantine2.ntic.als.event;

import dz.edu.univconstantine2.ntic.als.model.MasteryState;

public record QuizCompletedEvent(String enrollmentId, String moduleId, int score, int threshold, MasteryState newState) {}
