package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record SiteSettingsAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String churchName, String address, BigDecimal latitude, BigDecimal longitude, String email, String phone, Map<String, Object> socials, String heroHeadline, UUID heroImageId, String heroCtaText, String heroCtaUrl, String footerText) {}
