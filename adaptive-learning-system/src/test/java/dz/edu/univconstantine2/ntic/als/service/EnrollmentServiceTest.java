package dz.edu.univconstantine2.ntic.als.service;

import dz.edu.univconstantine2.ntic.als.dto.EnrollmentResponseDTO;
import dz.edu.univconstantine2.ntic.als.model.Course;
import dz.edu.univconstantine2.ntic.als.model.Enrollment;
import dz.edu.univconstantine2.ntic.als.model.MasteryState;
import dz.edu.univconstantine2.ntic.als.model.Module;
import dz.edu.univconstantine2.ntic.als.model.User;
import dz.edu.univconstantine2.ntic.als.repository.CourseRepository;
import dz.edu.univconstantine2.ntic.als.repository.EnrollmentRepository;
import dz.edu.univconstantine2.ntic.als.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;








@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService unit tests")
class EnrollmentServiceTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private EnrollmentService enrollmentService;

    

    private User learner;
    private Course course;
    private Module module1;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(
                enrollmentRepository, courseRepository, userRepository, eventPublisher);

        learner = User.builder()
                .id(1L).name("Alice").email("alice@test.com")
                .password("hashed").role("LEARNER").build();

        module1 = Module.builder().id("mod-1").title("Intro").displayOrder(1).build();

        course = Course.builder()
                .id("course-1").title("Test Course")
                .modules(List.of(module1))
                .build();
        module1.setCourse(course);
    }

    

    @Test
    @DisplayName("Enrolling twice in the same course throws IllegalStateException")
    void enroll_duplicateEnrollment_throwsIllegalStateException() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(learner));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByUserAndCourse(learner, course))
                .thenReturn(Optional.of(Enrollment.builder().id("existing").build()));

        assertThatThrownBy(() -> enrollmentService.enroll("course-1", "alice@test.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already enrolled");

        
        verify(enrollmentRepository, never()).save(any());
    }

    

    @Test
    @DisplayName("completeModule with score below threshold sets masteryState to NEEDS_REMEDIATION")
    void completeModule_scoreBelowThreshold_setsNeedsRemediation() {
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1").course(course).completedModuleIds("").score(0)
                .masteryState(MasteryState.IN_PROGRESS).build();

        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EnrollmentResponseDTO result =
                enrollmentService.completeModule("enroll-1", "mod-1", 30, 70);

        assertThat(result.masteryState()).isEqualTo(MasteryState.NEEDS_REMEDIATION.name());
    }

    @Test
    @DisplayName("completeModule with score at threshold adds moduleId to completedModuleIds")
    void completeModule_scoreAtThreshold_addsModuleToCompleted() {
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1").course(course).completedModuleIds("").score(0)
                .masteryState(MasteryState.IN_PROGRESS).build();

        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EnrollmentResponseDTO result =
                enrollmentService.completeModule("enroll-1", "mod-1", 70, 70);

        assertThat(result.completedModuleIds()).contains("mod-1");
    }

    @Test
    @DisplayName("completeModule with score above threshold adds moduleId to completedModuleIds")
    void completeModule_scoreAboveThreshold_addsModuleToCompleted() {
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1").course(course).completedModuleIds("").score(0)
                .masteryState(MasteryState.IN_PROGRESS).build();

        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EnrollmentResponseDTO result =
                enrollmentService.completeModule("enroll-1", "mod-1", 95, 70);

        assertThat(result.completedModuleIds()).contains("mod-1");
    }
}
