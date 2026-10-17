package za.co.heritagebaptist.forge;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import za.co.heritagebaptist.forge.config.JpaConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
@Import(JpaConfig.class)
@Testcontainers
class DevSeedTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired JdbcTemplate jdbc;
    @Autowired DataSource dataSource;
    @Autowired Flyway flyway;

    @Test
    void developmentProfileLoadsRequiredExamplesWithoutPrivateData() {
        assertThat(flyway.info().applied()).hasSize(5);
        assertCount("service_time", 2);
        assertCount("ministry", 4);
        assertCount("giving_fund", 3);
        assertCount("preacher", 2);
        assertCount("sermon_series", 1);
        assertCount("sermon", 3);
        assertCount("blog_post", 2);
        assertCount("faq", 3);
        assertCount("page", 6);
        assertCount("site_settings", 1);

        assertThat(jdbc.queryForList("select to_char(time, 'HH24:MI') from service_time order by time", String.class))
            .containsExactly("09:30", "18:00");
        assertThat(jdbc.queryForList("select slug from ministry", String.class))
            .containsExactlyInAnyOrder("growth-groups", "young-adults", "womens-ministry", "mens-ministry");
        assertThat(jdbc.queryForList("select slug from giving_fund", String.class))
            .containsExactlyInAnyOrder("general", "new-church-building-fund", "church-planting-fund");
        assertThat(jdbc.queryForObject(
            "select count(*) from giving_fund where bank = 'DUMMY BANK' and account_name = 'DUMMY ACCOUNT' "
                + "and branch_code = '000000' and account_number = '0000000000'", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("select count(*) from sermon where audio_id is not null", Integer.class)).isZero();
        assertThat(jdbc.queryForList("select slug from page", String.class)).contains(
            "a-brief-history", "1689-baptist-confession", "potchefstroom-church-plant", "bible-hour", "do-you-need-a-lift");
        assertThat(jdbc.queryForObject("select body from page where slug = '1689-baptist-confession'", String.class))
            .contains("## Chapter 1:", "## Chapter 2:", "Development placeholders");
        assertThat(jdbc.queryForObject("select address from site_settings", String.class))
            .isEqualTo("41 7th Avenue, Melville, Johannesburg");

        for (String table : new String[]{"app_user", "refresh_token", "contact_message", "issue_report", "audit_log", "sync_outbox"}) {
            assertCount(table, 0);
        }
    }

    @Test
    void rerunningSeedPreservesDeveloperEditsAndDoesNotDuplicateRows() {
        jdbc.update("update faq set answer = 'Locally edited answer' where id = '00000000-0000-4000-8000-000000000070'");
        new ResourceDatabasePopulator(new ClassPathResource("db/dev/R__dev_seed.sql")).execute(dataSource);
        assertCount("faq", 3);
        assertCount("site_settings", 1);
        assertCount("sermon", 3);
        assertThat(jdbc.queryForObject(
            "select answer from faq where id = '00000000-0000-4000-8000-000000000070'", String.class))
            .isEqualTo("Locally edited answer");
    }

    private void assertCount(String table, int expected) {
        assertThat(jdbc.queryForObject("select count(*) from " + table, Integer.class))
            .as("Row count in %s", table).isEqualTo(expected);
    }
}
