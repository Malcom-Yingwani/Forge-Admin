package za.co.heritagebaptist.forge;

import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.auth.AppUser;
import za.co.heritagebaptist.forge.auth.RefreshToken;
import za.co.heritagebaptist.forge.content.preacher.Preacher;
import za.co.heritagebaptist.forge.content.sermon.SermonSeries;
import za.co.heritagebaptist.forge.content.sermon.Sermon;
import za.co.heritagebaptist.forge.content.ministry.Ministry;
import za.co.heritagebaptist.forge.content.event.Event;
import za.co.heritagebaptist.forge.content.page.Page;
import za.co.heritagebaptist.forge.content.person.Person;
import za.co.heritagebaptist.forge.content.blog.BlogPost;
import za.co.heritagebaptist.forge.content.document.Document;
import za.co.heritagebaptist.forge.content.giving.GivingFund;
import za.co.heritagebaptist.forge.content.growthgroup.GrowthGroup;
import za.co.heritagebaptist.forge.content.servicetime.ServiceTime;
import za.co.heritagebaptist.forge.content.settings.SiteSettings;
import za.co.heritagebaptist.forge.inbox.ContactMessage;
import za.co.heritagebaptist.forge.issue.IssueReport;
import za.co.heritagebaptist.forge.common.AuditLog;
import za.co.heritagebaptist.forge.sync.SyncOutbox;
import za.co.heritagebaptist.forge.content.faq.Faq;
import za.co.heritagebaptist.forge.content.sermon.ScriptureBook;
import za.co.heritagebaptist.forge.content.sermon.ServiceType;
import za.co.heritagebaptist.forge.content.document.DocumentCategory;
import za.co.heritagebaptist.forge.content.person.Office;
import za.co.heritagebaptist.forge.auth.Role;
import za.co.heritagebaptist.forge.config.JpaConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@Testcontainers
class DomainPersistenceTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    EntityManager entityManager;

    @Autowired ApplicationContext applicationContext;
    @Autowired JdbcTemplate jdbc;

    @ParameterizedTest(name = "{0} saves and reloads")
    @ValueSource(strings = {"MediaAsset", "AppUser", "RefreshToken", "Preacher", "SermonSeries", "Sermon", "Ministry", "Event", "Page", "Person", "BlogPost", "Document", "GivingFund", "GrowthGroup", "ServiceTime", "SiteSettings", "ContactMessage", "IssueReport", "AuditLog", "SyncOutbox", "Faq"})
    void savesAndReloadsEachEntity(String name) {
        BaseEntity original = fixture(name);
        UUID id = original.getId();
        assertThat(id).isNotNull();
        assertThat(original.isNew()).isTrue();
        repositoryFor(original).saveAndFlush(original);
        entityManager.clear();

        BaseEntity loaded = entityManager.find(original.getClass(), id);
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isEqualTo(id);
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getUpdatedAt()).isNotNull();
        assertThat(loaded.isNew()).isFalse();
        assertThat(loaded.getVersion()).isZero();

        if (loaded instanceof Faq faq) {
            assertThat(faq.getAnswer()).hasSizeGreaterThan(255);
            assertThat(faq.isPublished()).isTrue();
        } else if (loaded instanceof Sermon sermon) {
            assertThat(sermon.getPreacher().getSlug()).isEqualTo("related-preacher");
            assertThat(sermon.getScriptureBook()).isEqualTo(ScriptureBook.GENESIS);
        } else if (loaded instanceof GivingFund fund) {
            assertThat(fund.getTargetAmount()).isEqualByComparingTo("1234.56");
        } else if (loaded instanceof BlogPost post) {
            assertThat(post.getTags()).containsExactly("church", "news");
        } else if (loaded instanceof MediaAsset media) {
            assertThat(media.getVariants()).containsEntry("original", "test/audio.mp3");
        } else if (loaded instanceof SiteSettings settings) {
            assertThat(settings.getSocials()).containsEntry("website", "https://example.invalid");
        } else if (loaded instanceof SyncOutbox outbox) {
            assertThat(outbox.getPayload()).containsEntry("question", "Test question");
            assertThat(outbox.getStatus()).isEqualTo(za.co.heritagebaptist.forge.sync.OutboxStatus.PENDING);
        } else if (loaded instanceof ServiceTime service) {
            assertThat(service.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
            assertThat(service.getTime()).isEqualTo(LocalTime.of(9, 30));
        }
    }

    @Test
    void updatingContentAdvancesItsVersion() {
        Faq faq = (Faq) fixture("Faq");
        entityManager.persist(faq);
        entityManager.flush();
        long before = faq.getVersion();
        faq.setAnswer("Updated answer");
        entityManager.flush();
        entityManager.clear();
        Faq loaded = entityManager.find(Faq.class, faq.getId());
        assertThat(loaded.getVersion()).isGreaterThan(before);
        assertThat(loaded.getAnswer()).isEqualTo("Updated answer");
    }

    @Test
    void duplicateSlugsAreRejectedByDatabase() {
        entityManager.persist(fixture("Preacher"));
        entityManager.flush();
        entityManager.persist(fixture("Preacher"));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(jakarta.persistence.PersistenceException.class);
    }

    @Test
    void onlyOneSiteSettingsRowIsAllowed() {
        entityManager.persist(fixture("SiteSettings"));
        entityManager.flush();
        entityManager.persist(fixture("SiteSettings"));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(jakarta.persistence.PersistenceException.class);
    }

    @Test
    void nonDevelopmentProfileDoesNotLoadSeed() {
        assertThat(jdbc.queryForObject(
            "select count(*) from flyway_schema_history where script = 'R__dev_seed.sql'", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("select count(*) from faq", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("select count(*) from site_settings", Integer.class)).isZero();
    }

    @SuppressWarnings("unchecked")
    private JpaRepository<BaseEntity, UUID> repositoryFor(BaseEntity entity) {
        String name = entity.getClass().getSimpleName();
        String beanName = Character.toLowerCase(name.charAt(0)) + name.substring(1) + "Repository";
        return (JpaRepository<BaseEntity, UUID>) applicationContext.getBean(beanName, JpaRepository.class);
    }
    private AppUser user() {
        AppUser value = new AppUser();
        value.setEmail("related@example.invalid");
        value.setDisplayName("Related Test User");
        value.setPasswordHash("test-only-not-a-real-password-hash");
        value.setRole(Role.EDITOR);
        entityManager.persist(value);
        return value;
    }

    private Preacher preacher() {
        Preacher value = new Preacher();
        value.setName("Related Test Preacher");
        value.setSlug("related-preacher");
        entityManager.persist(value);
        return value;
    }

    private BaseEntity fixture(String name) {
        switch (name) {
            case "MediaAsset": {
                MediaAsset value = new MediaAsset();
                value.setStorageKey("test/audio.mp3");
                value.setOriginalName("audio.mp3");
                value.setContentType("audio/mpeg");
                value.setSize(1234);
                value.setSha256("a".repeat(64));
                value.setVariants(Map.of("original", "test/audio.mp3"));
                return value;
            }
            case "AppUser": {
                AppUser value = new AppUser();
                value.setEmail("editor@example.invalid");
                value.setPasswordHash("test-only-not-a-real-password-hash");
                value.setDisplayName("Test Editor");
                value.setRole(Role.EDITOR);
                return value;
            }
            case "RefreshToken": {
                RefreshToken value = new RefreshToken();
                value.setUser(user());
                value.setTokenHash("test-only-token-hash");
                value.setExpiresAt(Instant.parse("2030-01-01T00:00:00Z"));
                return value;
            }
            case "Preacher": {
                Preacher value = new Preacher();
                value.setName("Test Preacher");
                value.setSlug("test-preacher");
                return value;
            }
            case "SermonSeries": {
                SermonSeries value = new SermonSeries();
                value.setTitle("Test Series");
                value.setSlug("test-series");
                return value;
            }
            case "Sermon": {
                Sermon value = new Sermon();
                value.setTitle("Test Sermon");
                value.setSlug("test-sermon");
                value.setPreachedOn(LocalDate.of(2026, 10, 17));
                value.setService(ServiceType.MORNING);
                value.setPreacher(preacher());
                value.setScriptureBook(ScriptureBook.GENESIS);
                return value;
            }
            case "Ministry": {
                Ministry value = new Ministry();
                value.setName("Test Ministry");
                value.setSlug("test-ministry");
                return value;
            }
            case "Event": {
                Event value = new Event();
                value.setTitle("Test Event");
                value.setSlug("test-event");
                value.setStartsAt(Instant.parse("2026-10-18T07:30:00Z"));
                return value;
            }
            case "Page": {
                Page value = new Page();
                value.setTitle("Test Page");
                value.setSlug("test-page");
                value.setBody("# Test page");
                value.setSection("ABOUT");
                return value;
            }
            case "Person": {
                Person value = new Person();
                value.setName("Test Elder");
                value.setCategory("LEADERSHIP");
                value.setOffice(Office.ELDER);
                return value;
            }
            case "BlogPost": {
                BlogPost value = new BlogPost();
                value.setTitle("Test Post");
                value.setSlug("test-post");
                value.setBody("# Test post");
                value.setTags(List.of("church", "news"));
                return value;
            }
            case "Document": {
                Document value = new Document();
                value.setTitle("Test Document");
                value.setSlug("test-document");
                value.setCategory(DocumentCategory.CREED);
                return value;
            }
            case "GivingFund": {
                GivingFund value = new GivingFund();
                value.setName("Test Fund");
                value.setSlug("test-fund");
                value.setAccountName("DUMMY");
                value.setBank("DUMMY");
                value.setBranchCode("000000");
                value.setAccountNumber("0000000000");
                value.setAccountType("DUMMY");
                value.setTargetAmount(new BigDecimal("1234.56"));
                return value;
            }
            case "GrowthGroup": {
                GrowthGroup value = new GrowthGroup();
                value.setName("Test Group");
                value.setDayOfWeek(DayOfWeek.WEDNESDAY);
                value.setTime(LocalTime.of(18, 30));
                return value;
            }
            case "ServiceTime": {
                ServiceTime value = new ServiceTime();
                value.setName("Morning Service");
                value.setDayOfWeek(DayOfWeek.SUNDAY);
                value.setTime(LocalTime.of(9, 30));
                return value;
            }
            case "SiteSettings": {
                SiteSettings value = new SiteSettings();
                value.setChurchName("Test Church");
                value.setAddress("Test address");
                value.setSocials(Map.of("website", "https://example.invalid"));
                return value;
            }
            case "ContactMessage": {
                ContactMessage value = new ContactMessage();
                value.setCategory("GENERAL");
                value.setName("Test Sender");
                value.setEmail("sender@example.invalid");
                value.setMessage("Test message");
                value.setReceivedAt(Instant.parse("2026-10-17T12:00:00Z"));
                return value;
            }
            case "IssueReport": {
                IssueReport value = new IssueReport();
                value.setReporter(user());
                value.setCategory("BUG");
                value.setDescription("Test report");
                return value;
            }
            case "AuditLog": {
                AuditLog value = new AuditLog();
                value.setAction("CREATE");
                value.setEntityType("faq");
                value.setEntityId(UUID.randomUUID());
                value.setAt(Instant.parse("2026-10-17T12:00:00Z"));
                value.setDiff(Map.of("question", "Test question"));
                return value;
            }
            case "SyncOutbox": {
                SyncOutbox value = new SyncOutbox();
                value.setKind("EVENT");
                value.setType("faq");
                value.setEntityId(UUID.randomUUID());
                value.setPayload(Map.of("question", "Test question"));
                return value;
            }
            case "Faq": {
                Faq value = new Faq();
                value.setQuestion("Test question?");
                value.setAnswer("An answer longer than a default character column. ".repeat(20));
                value.setPublished(true);
                return value;
            }
            default:
                throw new IllegalArgumentException("Unknown fixture: " + name);
        }
    }
}
