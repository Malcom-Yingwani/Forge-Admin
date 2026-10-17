package za.co.heritagebaptist.forge.common;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class SlugService {
    public String slugify(String title) {
        String normalized = Normalizer.normalize(title == null ? "" : title, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            return "untitled";
        }
        return normalized.substring(0, Math.min(normalized.length(), 255)).replaceAll("-$", "");
    }

    // The caller supplies the existence check for its content repository.
    // Database uniqueness remains the final protection against concurrent inserts.
    public String uniqueSlug(String title, Predicate<String> exists) {
        String base = slugify(title);
        String candidate = base;
        int suffix = 2;
        while (exists.test(candidate)) {
            String ending = "-" + suffix++;
            candidate = base.substring(0, Math.min(base.length(), 255 - ending.length())) + ending;
        }
        return candidate;
    }
}
