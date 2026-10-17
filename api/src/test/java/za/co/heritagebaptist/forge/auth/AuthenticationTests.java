package za.co.heritagebaptist.forge.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.*;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "management.health.mail.enabled=false")
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc(print = org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.NONE)
@ActiveProfiles({"dev", "test"})
@Testcontainers
@ExtendWith(OutputCaptureExtension.class)
class AuthenticationTests {
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    @Autowired MockMvc mvc;
    @Autowired AppUserRepository users;
    @Autowired RefreshTokenRepository tokens;
    @Autowired RefreshTokenService refresh;
    @Autowired PasswordEncoder passwords;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean JavaMailSender mail;
    AppUser user;
    String email;
    static final String PASSWORD = "test-only-password-123";
    @BeforeEach void setup() {
        email = UUID.randomUUID() + "@example.test";
        user = new AppUser(); user.setEmail(email); user.setDisplayName("Test Editor");
        user.setPasswordHash(passwords.encode(PASSWORD)); user.setEnabled(true); user.setRole(Role.EDITOR);
        user = users.saveAndFlush(user);
    }
    MvcResult login() throws Exception {
        return mvc.perform(post("/api/auth/login").contentType("application/json")
            .content(json.writeValueAsString(new AuthController.Login(email, PASSWORD))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.user.role").value("EDITOR"))
            .andExpect(jsonPath("$.user.passwordHash").doesNotExist()).andReturn();
    }
    String access(MvcResult result) throws Exception { return json.readTree(result.getResponse().getContentAsString()).get("accessToken").asText(); }
    Cookie cookie(MvcResult result) { return result.getResponse().getCookie("refresh_token"); }
    @Test void loginMeRoleRulesAndLogout(CapturedOutput output) throws Exception {
        mvc.perform(get("/api/admin/sermons")).andExpect(status().isUnauthorized());
        var logged = login(); String bearer = "Bearer " + access(logged);
        assertThat(cookie(logged).isHttpOnly()).isTrue();
        assertThat(logged.getResponse().getHeader("Set-Cookie")).contains("SameSite=Strict", "Path=/api/auth");
        mvc.perform(get("/api/auth/me").header("Authorization", bearer)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email));
        mvc.perform(get("/api/admin/users").header("Authorization", bearer)).andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/sync/resync").header("Authorization", bearer)).andExpect(status().isForbidden());
        mvc.perform(get("/api/auth/me").header("Authorization", bearer + "tampered")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/logout").cookie(cookie(logged))).andExpect(status().isNoContent())
            .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
        mvc.perform(post("/api/auth/refresh").cookie(cookie(logged))).andExpect(status().isUnauthorized());
        assertThat(output.getAll()).doesNotContain(PASSWORD, access(logged), cookie(logged).getValue());
    }
    @Test void rotatedTokenReuseRevokesEverySessionAndOnlyHashesAreStored() throws Exception {
        var first = login(); var other = login();
        var rotated = mvc.perform(post("/api/auth/refresh").cookie(cookie(first))).andExpect(status().isOk()).andReturn();
        assertThat(cookie(rotated).getValue()).isNotEqualTo(cookie(first).getValue());
        assertThat(jdbc.queryForList("select token_hash from refresh_token where user_id = ?", String.class, user.getId()))
            .allMatch(value -> value.matches("[a-f0-9]{64}")).doesNotContain(cookie(first).getValue());
        mvc.perform(post("/api/auth/refresh").cookie(cookie(first))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").cookie(cookie(rotated))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").cookie(cookie(other))).andExpect(status().isUnauthorized());
        assertThat(jdbc.queryForObject("select count(*) from refresh_token where user_id = ? and not revoked", Integer.class, user.getId())).isZero();
    }
    @Test void sixthBadLoginIsRateLimited() throws Exception {
        for (int i=0; i<5; i++) mvc.perform(post("/api/auth/login").contentType("application/json")
            .content(json.writeValueAsString(new AuthController.Login(email, "wrong"))))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType("application/json")
            .content(json.writeValueAsString(new AuthController.Login(email, PASSWORD))))
            .andExpect(status().isTooManyRequests()).andExpect(header().exists("Retry-After"));
    }
    @Test void forgotIsUniformAndResetIsSingleUseAndRevokesSessions() throws Exception {
        var logged = login();
        mvc.perform(post("/api/auth/forgot-password").contentType("application/json").content("{\"email\":\"unknown@example.test\"}"))
            .andExpect(status().isAccepted());
        verifyNoInteractions(mail);
        mvc.perform(post("/api/auth/forgot-password").contentType("application/json").content("{\"email\":\"" + email + "\"}"))
            .andExpect(status().isAccepted());
        var message = ArgumentCaptor.forClass(SimpleMailMessage.class); verify(mail).send(message.capture());
        String secret = message.getValue().getText().split("token=")[1];
        String body = json.writeValueAsString(new AuthController.Reset(secret, "replacement-password-123"));
        mvc.perform(post("/api/auth/reset-password").contentType("application/json").content(body)).andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/reset-password").contentType("application/json").content(body)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").cookie(cookie(logged))).andExpect(status().isUnauthorized());
        assertThat(passwords.matches("replacement-password-123", users.findById(user.getId()).orElseThrow().getPasswordHash())).isTrue();
    }
    @Test void expiredRefreshAndDisabledUserAreRejected() throws Exception {
        var logged = login();
        jdbc.update("update refresh_token set expires_at = now() - interval '1 day' where user_id = ?", user.getId());
        mvc.perform(post("/api/auth/refresh").cookie(cookie(logged))).andExpect(status().isUnauthorized());
        user = users.findById(user.getId()).orElseThrow();
        user.setEnabled(false); users.saveAndFlush(user);
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + access(logged))).andExpect(status().isUnauthorized());
    }
    @Test void profileRequiresCurrentPasswordAndUpdatesOwnAccount() throws Exception {
        String bearer = "Bearer " + access(login());
        var request = new AuthController.Profile("Changed Name", email, "wrong", null);
        mvc.perform(put("/api/auth/me").header("Authorization", bearer).contentType("application/json")
            .content(json.writeValueAsString(request))).andExpect(status().isUnauthorized());
        request = new AuthController.Profile("Changed Name", email, PASSWORD, null);
        mvc.perform(put("/api/auth/me").header("Authorization", bearer).contentType("application/json")
            .content(json.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Changed Name"));
    }
}

