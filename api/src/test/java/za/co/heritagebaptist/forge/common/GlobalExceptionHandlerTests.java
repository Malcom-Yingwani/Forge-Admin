package za.co.heritagebaptist.forge.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTests {
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .addFilters(new RequestLoggingFilter()).build();
    }

    @Test
    void mapsEveryRequiredExceptionAndEchoesRequestId() throws Exception {
        String[] cases = {"missing", "conflict", "forbidden", "upload", "unexpected"};
        int[] statuses = {404, 409, 403, 413, 500};
        for (int i = 0; i < cases.length; i++) {
            var result = mvc.perform(get("/test/" + cases[i]).header("X-Request-Id", "test-" + cases[i]))
                .andExpect(status().is(statuses[i]))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(statuses[i]))
                .andExpect(header().string("X-Request-Id", "test-" + cases[i])).andReturn();
            assertThat(result.getResponse().getContentAsString()).doesNotContain("secret-database-value", "stackTrace");
        }
        mvc.perform(get("/test/conflict")).andExpect(jsonPath("$.detail")
            .value("Edited by someone else, reload and try again"));
    }

    @Test
    void returnsFieldValidationErrorsAndHandlesMalformedJson() throws Exception {
        mvc.perform(post("/test").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors[0].field").value("title"))
            .andExpect(header().exists("X-Request-Id"));
        mvc.perform(post("/test").contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void replacesUnsafeRequestIdentifiers() throws Exception {
        mvc.perform(get("/test/missing").header("X-Request-Id", "x".repeat(101)))
            .andExpect(header().string("X-Request-Id", org.hamcrest.Matchers.matchesPattern(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")));
    }

    record Input(@NotBlank String title) {}

    @RestController
    static class TestController {
        @PostMapping("/test")
        void validate(@Valid @RequestBody Input input) {}

        @GetMapping("/test/{kind}")
        void fail(@PathVariable String kind) {
            switch (kind) {
                case "missing" -> throw new NotFoundException("Record not found");
                case "conflict" -> throw new ObjectOptimisticLockingFailureException("Faq", 1);
                case "forbidden" -> throw new AccessDeniedException("Access denied");
                case "upload" -> throw new MaxUploadSizeExceededException(1);
                default -> throw new IllegalStateException("secret-database-value");
            }
        }
    }
}
