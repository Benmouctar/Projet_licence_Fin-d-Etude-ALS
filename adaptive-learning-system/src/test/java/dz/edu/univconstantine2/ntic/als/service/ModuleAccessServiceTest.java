package dz.edu.univconstantine2.ntic.als.service;

import dz.edu.univconstantine2.ntic.als.dto.ModuleAccessDTO;
import dz.edu.univconstantine2.ntic.als.model.Course;
import dz.edu.univconstantine2.ntic.als.model.Enrollment;
import dz.edu.univconstantine2.ntic.als.model.MasteryState;
import dz.edu.univconstantine2.ntic.als.model.Module;
import dz.edu.univconstantine2.ntic.als.repository.EnrollmentRepository;
import dz.edu.univconstantine2.ntic.als.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;







@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleAccessService unit tests")
class ModuleAccessServiceTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private ModuleRepository moduleRepository;

    private ModuleAccessService moduleAccessService;

    

    private Course course;
    private Module module1;
    private Module module2;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        moduleAccessService = new ModuleAccessService(enrollmentRepository, moduleRepository);

        course = Course.builder().id("course-1").title("Test Course").build();

        module1 = Module.builder().id("mod-1").displayOrder(1).title("Module 1").course(course).build();
        module2 = Module.builder().id("mod-2").displayOrder(2).title("Module 2").course(course).build();

        enrollment = Enrollment.builder()
                .id("enroll-1")
                .course(course)
                .completedModuleIds("")
                .masteryState(MasteryState.IN_PROGRESS)
                .build();
    }

    

    @Test
    @DisplayName("First module (lowest displayOrder) is always accessible")
    void canAccess_firstModule_alwaysAccessible() {
        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(moduleRepository.findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc("course-1"))
                .thenReturn(List.of(module1, module2));

        ModuleAccessDTO result = moduleAccessService.canAccess("enroll-1", "mod-1");

        assertThat(result.canAccess()).isTrue();
        assertThat(result.reason()).containsIgnoringCase("always accessible");
    }

    @Test
    @DisplayName("Second module is NOT accessible when first module is not completed")
    void canAccess_secondModule_notAccessibleWhenPrerequisiteIncomplete() {
        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(moduleRepository.findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc("course-1"))
                .thenReturn(List.of(module1, module2));

        ModuleAccessDTO result = moduleAccessService.canAccess("enroll-1", "mod-2");

        assertThat(result.canAccess()).isFalse();
        assertThat(result.reason()).containsIgnoringCase("Module 1");
    }

    @Test
    @DisplayName("Second module IS accessible when first module is in completedModuleIds")
    void canAccess_secondModule_accessibleWhenPrerequisiteComplete() {
        enrollment = Enrollment.builder()
                .id("enroll-1")
                .course(course)
                .completedModuleIds("mod-1")
                .masteryState(MasteryState.IN_PROGRESS)
                .build();

        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(moduleRepository.findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc("course-1"))
                .thenReturn(List.of(module1, module2));

        ModuleAccessDTO result = moduleAccessService.canAccess("enroll-1", "mod-2");

        assertThat(result.canAccess()).isTrue();
    }

    @Test
    @DisplayName("Reason mentions AI Tutor when masteryState is NEEDS_REMEDIATION")
    void canAccess_needsRemediationState_reasonMentionsAITutor() {
        enrollment = Enrollment.builder()
                .id("enroll-1")
                .course(course)
                .completedModuleIds("")
                .masteryState(MasteryState.NEEDS_REMEDIATION)
                .build();

        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(moduleRepository.findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc("course-1"))
                .thenReturn(List.of(module1, module2));

        ModuleAccessDTO result = moduleAccessService.canAccess("enroll-1", "mod-2");

        assertThat(result.canAccess()).isFalse();
        assertThat(result.reason()).containsIgnoringCase("AI Tutor");
    }
}
