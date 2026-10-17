package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public record SiteSettingsPublicDto(
    UUID id,
    long version,
    String churchName,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    String email,
    String phone,
    Map<String, Object> socials,
    String heroHeadline,
    String heroImageKey,
    String heroCtaText,
    String heroCtaUrl,
    String footerText) {}
