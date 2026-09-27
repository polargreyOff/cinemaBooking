package gradle.example.com.cinemabooking.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginIsNotRejectedByCsrfFilter() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"missing-user","password":"invalid-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerIsNotRejectedByCsrfFilter() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutRejectsCsrfCookieWithoutMatchingHeader() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("XSRF-TOKEN", "test-token")))
                .andExpect(status().isForbidden());
    }

    @Test
    void logoutAcceptsMatchingCsrfCookieAndHeader() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("XSRF-TOKEN", "test-token"))
                        .header("X-XSRF-TOKEN", "test-token"))
                .andExpect(status().isNoContent());
    }
}
