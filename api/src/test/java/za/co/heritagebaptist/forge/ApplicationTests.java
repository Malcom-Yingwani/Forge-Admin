package za.co.heritagebaptist.forge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Testcontainers
class ApplicationTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @Test
    void healthIncludesWorkingDatabaseAndMigratedSchema() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
        assertThat(jdbc.queryForObject("select count(*) from flyway_schema_history where success = true and version = '1'", Integer.class)).isEqualTo(1);
    }

    @Test
    void openApiAvailableInDevelopment() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
            .andExpect(jsonPath("$.openapi").exists());
    }

    @Test
    void applicationRoutesAreDeniedUntilTheirSecurityIsImplemented() throws Exception {
        mvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
        mvc.perform(post("/internal/sync").contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void corsAllowsOnlyConfiguredFrontend() throws Exception {
        mvc.perform(options("/actuator/health").header("Origin", "http://localhost:5174")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5174"));
        mvc.perform(options("/actuator/health").header("Origin", "https://untrusted.example")
                .header("Access-Control-Request-Method", "GET")).andExpect(status().isForbidden());
    }
}
