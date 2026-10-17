package za.co.heritagebaptist.forge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SeedProfileConfigurationTests {
    @Test
    void seedLocationIsEnabledByDevelopmentProfileOnly() {
        new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withPropertyValues("spring.profiles.active=dev")
            .run(context -> assertThat(context.getEnvironment().getProperty("spring.flyway.locations"))
                .isEqualTo("classpath:db/migration,classpath:db/dev"));

        new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withPropertyValues("spring.profiles.active=production")
            .run(context -> assertThat(context.getEnvironment().getProperty(
                "spring.flyway.locations", "classpath:db/migration"))
                .isEqualTo("classpath:db/migration"));
    }
}
