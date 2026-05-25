package dz.edu.univconstantine2.ntic.als.service;

import dz.edu.univconstantine2.ntic.als.dto.ModuleAccessDTO;
import dz.edu.univconstantine2.ntic.als.model.Enrollment;
import dz.edu.univconstantine2.ntic.als.model.MasteryState;
import dz.edu.univconstantine2.ntic.als.model.Module;
import dz.edu.univconstantine2.ntic.als.repository.EnrollmentRepository;
import dz.edu.univconstantine2.ntic.als.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


















/**
 * Service class containing business logic, validation rules, and transactional processing for Module Access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleAccessService {

    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;

    public ModuleAccessDTO canAccess(String enrollmentId, String moduleId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NoSuchElementException("Enrollment not found: " + enrollmentId));

        
        String courseId = enrollment.getCourse().getId();
        List<Module> modules = moduleRepository
                .findByCourseIdAndDeletedFalseOrderByDisplayOrderAsc(courseId);

        if (modules.isEmpty()) {
            return new ModuleAccessDTO(false, "Course has no modules.");
        }

        
        int targetIndex = -1;
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getId().equals(moduleId)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            return new ModuleAccessDTO(false, "Module not found in this course.");
        }

        
        if (targetIndex == 0) {
            return new ModuleAccessDTO(true, "First module is always accessible.");
        }

        
        boolean hasQuizModules = modules.stream()
                .anyMatch(m -> "quiz".equalsIgnoreCase(m.getType()));
        if (!hasQuizModules) {
            return new ModuleAccessDTO(true, "No assessments in this course — all modules accessible.");
        }

        
        
        Set<String> completedIds = parseCsv(enrollment.getCompletedModuleIds());

        for (int i = targetIndex - 1; i >= 0; i--) {
            Module predecessor = modules.get(i);

            
            if (!"quiz".equalsIgnoreCase(predecessor.getType())) {
                continue;
            }

            
            if (!completedIds.contains(predecessor.getId())) {
                String reason;
                if (enrollment.getMasteryState() == MasteryState.NEEDS_REMEDIATION) {
                    reason = String.format(
                            "Complete the AI Tutor session for \"%s\" before moving on.",
                            predecessor.getTitle());
                } else {
                    reason = String.format(
                            "You must pass \"%s\" before accessing this module.",
                            predecessor.getTitle());
                }
                return new ModuleAccessDTO(false, reason);
            }

            
            break;
        }

        return new ModuleAccessDTO(true, "Module accessible.");
    }

    private Set<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();
        return new HashSet<>(Arrays.asList(csv.split(",")));
    }
}
