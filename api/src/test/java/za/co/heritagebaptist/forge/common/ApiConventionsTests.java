package za.co.heritagebaptist.forge.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class ApiConventionsTests {
    @Test
    void preservesPagingMetadata() {
        var response = PageResponse.of(new PageImpl<>(List.of("one"), PageRequest.of(1, 20), 21));
        assertThat(response.content()).containsExactly("one");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isEqualTo(21);
        assertThat(response.totalPages()).isEqualTo(2);
    }

    @Test
    void serializesDatesInJohannesburgAndIgnoresUnknownProperties() throws Exception {
        var builder = new Jackson2ObjectMapperBuilder();
        new JacksonConfig().forgeJackson().customize(builder);
        ObjectMapper mapper = builder.build();
        assertThat(mapper.writeValueAsString(ZonedDateTime.parse("2026-10-17T10:00:00Z")))
            .isEqualTo("\"2026-10-17T12:00:00+02:00\"");
        assertThat(mapper.readValue("{\"title\":\"Test\",\"unknown\":true}", Input.class).title()).isEqualTo("Test");
    }

    record Input(String title) {}
}
