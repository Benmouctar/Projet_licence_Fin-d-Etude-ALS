package dz.edu.univconstantine2.ntic.als.controller;

import dz.edu.univconstantine2.ntic.als.model.User;
import dz.edu.univconstantine2.ntic.als.repository.UserRepository;
import dz.edu.univconstantine2.ntic.als.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;












@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("File upload security tests")
class FileUploadSecurityIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private String instructorCookie;

    


    private static final byte[] MINIMAL_PDF = (
            "%PDF-1.4\n1 0 obj\n<< /Type /Catalog >>\nendobj\nxref\n0 0\ntrailer\n<< >>\n%%EOF"
    ).getBytes();

    @BeforeEach
    void setUp() {
        User instructor = userRepository.findByEmail("uploader@test.com").orElseGet(() -> {
            User u = User.builder()
                    .name("Upload Tester").email("uploader@test.com")
                    .password(passwordEncoder.encode("pass")).role("INSTRUCTOR")
                    .initials("UT").build();
            return userRepository.save(u);
        });
        instructorCookie = "ALS_AUTH=" + jwtUtil.generateToken(instructor.getEmail());
    }

    
    
    
    

    @Test
    @DisplayName("Uploading file with '..' in the name returns 400")
    void upload_dotDotFilename_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../traversal.pdf", "application/pdf", MINIMAL_PDF);

        
        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .header("Cookie", instructorCookie))
                .andExpect(status().isBadRequest());
    }

    

    @Test
    @DisplayName("Uploading file with extension not in allowlist returns 400")
    void upload_disallowedExtension_returns400() throws Exception {
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "script.sh", "text/plain", "#!/bin/bash\necho evil".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .header("Cookie", instructorCookie))
                .andExpect(status().isBadRequest());
    }

    

    @Test
    @DisplayName("Uploading valid PDF returns 201 with UUID filename in response")
    void upload_validPdf_returns201WithUuidFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "lecture.pdf", "application/pdf", MINIMAL_PDF);

        var result = mockMvc.perform(multipart("/api/files/upload")
                .file(file)
                .header("Cookie", instructorCookie))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        
        assertThat(body).matches("(?s).*[0-9a-f\\-]{36}\\.pdf.*");
    }
}
