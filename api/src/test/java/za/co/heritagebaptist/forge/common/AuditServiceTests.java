package za.co.heritagebaptist.forge.common;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.IllegalTransactionStateException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import za.co.heritagebaptist.forge.config.JpaConfig;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({JpaConfig.class, AuditService.class})
@Testcontainers
class AuditServiceTests {
    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired AuditService service;
    @Autowired AuditLogRepository repository;

    @Test
    void writesDiffAndRollsBackWithCaller() {
        var diff = Map.<String, Object>of("title", List.of("old", "new"));
        UUID id = service.record(null, "UPDATE", "faq", UUID.randomUUID(), diff).getId();
        repository.flush();
        assertThat(repository.findById(id).orElseThrow().getDiff()).isEqualTo(diff);
        TestTransaction.flagForRollback();
        TestTransaction.end();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void refusesToWriteOutsideCallerTransaction() {
        TestTransaction.end();
        assertThatThrownBy(() -> service.record(null, "UPDATE", "faq", UUID.randomUUID(), Map.of()))
            .isInstanceOf(IllegalTransactionStateException.class);
        assertThat(repository.count()).isZero();
    }
}
