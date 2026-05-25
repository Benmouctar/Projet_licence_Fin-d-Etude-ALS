/**
 * Spring Application Event representing the occurrence of a Remediation Needed activity.
 */
package dz.edu.univconstantine2.ntic.als.event;

public record RemediationNeededEvent(String enrollmentId, String moduleId, int score) {}
