package dz.edu.univconstantine2.ntic.als.service;

import dz.edu.univconstantine2.ntic.als.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class containing business logic, validation rules, and transactional processing for Security.
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final CourseRepository courseRepository;

    public boolean isCourseInstructor(String courseId, String email) {
        return courseRepository.existsByIdAndInstructorEmail(courseId, email);
    }
}
