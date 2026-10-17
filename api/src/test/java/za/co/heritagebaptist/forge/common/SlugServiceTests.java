package za.co.heritagebaptist.forge.common;

import java.util.Set;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SlugServiceTests {
    private final SlugService service = new SlugService();

    @Test
    void normalizesAccentsAndPunctuation() {
        assertThat(service.slugify("A Well-Ordered Church")).isEqualTo("a-well-ordered-church");
        assertThat(service.slugify("  Grâce & Vérité! ")).isEqualTo("grace-verite");
        assertThat(service.slugify(null)).isEqualTo("untitled");
        assertThat(service.slugify("!!!")).isEqualTo("untitled");
    }

    @Test
    void appendsSuffixesAndKeepsColumnLength() {
        Set<String> existing = Set.of("church", "church-2");
        assertThat(service.uniqueSlug("Church", existing::contains)).isEqualTo("church-3");
        String longTitle = "a".repeat(300);
        assertThat(service.uniqueSlug(longTitle, candidate -> candidate.length() == 255 && !candidate.endsWith("-2")))
            .hasSize(255).endsWith("-2");
    }

    @Test
    void doesNotDependOnMachineLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertThat(service.slugify("ISAIAH")).isEqualTo("isaiah");
        } finally {
            Locale.setDefault(original);
        }
    }
}
