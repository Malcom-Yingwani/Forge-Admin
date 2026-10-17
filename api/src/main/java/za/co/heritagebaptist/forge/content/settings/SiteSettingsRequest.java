package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record SiteSettingsRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String churchName,
    @NotBlank @Size(max=200000) String address,
    @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
    @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
    @Size(max=255) @Email String email,
    @Size(max=255) String phone,
    Map<String, Object> socials,
    @Size(max=255) String heroHeadline,
    UUID heroImageId,
    @Size(max=255) String heroCtaText,
    @Size(max=200000) String heroCtaUrl,
    @Size(max=200000) String footerText) {}
