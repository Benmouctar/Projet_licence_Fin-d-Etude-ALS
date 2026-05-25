package dz.edu.univconstantine2.ntic.als.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dz.edu.univconstantine2.ntic.als.model.User;
import dz.edu.univconstantine2.ntic.als.repository.UserRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;










@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController integration tests")
class AuthControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL    = "/api/auth/login";
    private static final String ME_URL       = "/api/auth/me";

    @BeforeEach
    void seedUser() {
        if (userRepository.findByEmail("alice@test.com").isEmpty()) {
            User u = new User();
            u.setName("Alice Test");
            u.setEmail("alice@test.com");
            u.setPassword(passwordEncoder.encode("password123"));
            u.setRole("LEARNER");
            u.setInitials("AT");
            userRepository.save(u);
        }
    }

    

    @Test
    @DisplayName("POST /api/auth/login with valid credentials returns Set-Cookie with ALS_AUTH")
    void login_validCredentials_returnsAuthCookie() throws Exception {
        String body = """
                {"email":"alice@test.com","password":"password123"}
                """;

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(result -> {
                    String cookie = result.getResponse().getHeader("Set-Cookie");
                    assertThat(cookie).contains("ALS_AUTH");
                });
    }

    

    @Test
    @DisplayName("POST /api/auth/login response body does NOT contain a 'token' field")
    void login_response_doesNotContainTokenField() throws Exception {
        String body = """
                {"email":"alice@test.com","password":"password123"}
                """;

        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.has("token")).isFalse();
    }

    

    @Test
    @DisplayName("GET /api/auth/me with valid cookie returns 200 and user data")
    void me_withValidCookie_returns200AndUserData() throws Exception {
        
        String body = """
                {"email":"alice@test.com","password":"password123"}
                """;

        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
        assertThat(setCookieHeader).isNotNull();

        
        String cookieValue = setCookieHeader.split(";")[0]; 

        
        mockMvc.perform(get(ME_URL).header("Cookie", cookieValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@test.com"));
    }

    

    @Test
    @DisplayName("GET /api/auth/me without cookie returns 401")
    void me_withoutCookie_returns401() throws Exception {
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    @DisplayName("POST /api/auth/login with wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        String body = """
                {"email":"alice@test.com","password":"wrongpassword"}
                """;

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    @DisplayName("POST /api/auth/register with duplicate email returns 409")
    void register_duplicateEmail_returns409() throws Exception {
        String body = """
                {"name":"Alice Again","email":"alice@test.com","password":"somepassword"}
                """;

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());
    }
}
