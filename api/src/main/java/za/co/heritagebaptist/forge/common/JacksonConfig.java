package za.co.heritagebaptist.forge.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer forgeJackson() {
        return builder -> builder.timeZone(TimeZone.getTimeZone("Africa/Johannesburg"))
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .featuresToEnable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
    }
}
