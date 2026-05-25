/**
 * Spring Application Event representing the occurrence of a User Enrolled activity.
 */
package dz.edu.univconstantine2.ntic.als.event;

public record UserEnrolledEvent(String enrollmentId) {}
