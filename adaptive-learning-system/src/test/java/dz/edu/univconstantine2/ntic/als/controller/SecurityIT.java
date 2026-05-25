package dz.edu.univconstantine2.ntic.als.controller;

import dz.edu.univconstantine2.ntic.als.model.Course;
import dz.edu.univconstantine2.ntic.als.model.User;
import dz.edu.univconstantine2.ntic.als.repository.CourseRepository;
import dz.edu.univconstantine2.ntic.als.repository.UserRepository;
import dz.edu.univconstantine2.ntic.als.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;














@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Security integration tests")
class SecurityIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private String instructorCookie;
    private String otherInstructorCookie;
    private String adminCookie;
    private String learnerCookie;
    private String courseId;

    @BeforeEach
    void setUp() {
        String encoded = passwordEncoder.encode("password");

        User instructor = createUser("instructor@sec.test", "INSTRUCTOR", encoded);
        createUser("other@sec.test", "INSTRUCTOR", encoded);
        createUser("admin@sec.test", "ADMIN", encoded);
        createUser("learner@sec.test", "LEARNER", encoded);

        
        Course course = Course.builder()
                .title("Security Test Course")
                .category("Testing")
                .description("desc")
                .gradient("from-blue-500 to-indigo-600")
                .instructor(instructor)
                .build();
        course = courseRepository.save(course);
        courseId = course.getId();

        instructorCookie      = buildCookie(jwtUtil.generateToken("instructor@sec.test"));
        otherInstructorCookie = buildCookie(jwtUtil.generateToken("other@sec.test"));
        adminCookie           = buildCookie(jwtUtil.generateToken("admin@sec.test"));
        learnerCookie         = buildCookie(jwtUtil.generateToken("learner@sec.test"));
    }

    

    private User createUser(String email, String role, String encoded) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .name(role + " User").email(email)
                    .password(encoded).role(role).initials("TU").build();
            return userRepository.save(u);
        });
    }

    private String buildCookie(String jwt) {
        return "ALS_AUTH=" + jwt;
    }

    

    @Test
    @DisplayName("DELETE /api/courses/{id} as the course's instructor returns 204")
    void deleteCourse_asOwnerInstructor_returns204() throws Exception {
        mockMvc.perform(delete("/api/courses/" + courseId)
                .header("Cookie", instructorCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/courses/{id} as a DIFFERENT instructor returns 403")
    void deleteCourse_asDifferentInstructor_returns403() throws Exception {
        mockMvc.perform(delete("/api/courses/" + courseId)
                .header("Cookie", otherInstructorCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/courses/{id} as ADMIN returns 204")
    void deleteCourse_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/courses/" + courseId)
                .header("Cookie", adminCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/admin/users with LEARNER token returns 403")
    void adminEndpoint_asLearner_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .header("Cookie", learnerCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Protected endpoint without authentication returns 401")
    void protectedEndpoint_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
