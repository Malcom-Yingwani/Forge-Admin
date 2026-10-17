package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.settings.SiteSettings;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class SiteSettingsMapper implements ContentMapper<SiteSettings,SiteSettingsRequest,SiteSettingsAdminDto,SiteSettingsPublicDto> {
    private final ContentSupport support;
    public SiteSettingsMapper(ContentSupport support) { this.support=support; }
    public void apply(SiteSettings e,SiteSettingsRequest r) {
        e.setChurchName(r.churchName());
        e.setAddress(r.address());
        e.setLatitude(r.latitude());
        e.setLongitude(r.longitude());
        e.setEmail(r.email());
        e.setPhone(r.phone());
        e.setSocials(r.socials());
        e.setHeroHeadline(r.heroHeadline());
        e.setHeroImage(support.reference(MediaAsset.class,r.heroImageId()));
        e.setHeroCtaText(r.heroCtaText());
        e.setHeroCtaUrl(r.heroCtaUrl());
        e.setFooterText(r.footerText());
    }
    public SiteSettingsAdminDto admin(SiteSettings e) { return new SiteSettingsAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getChurchName(), e.getAddress(), e.getLatitude(), e.getLongitude(), e.getEmail(), e.getPhone(), e.getSocials(), e.getHeroHeadline(), e.getHeroImage() == null ? null : e.getHeroImage().getId(), e.getHeroCtaText(), e.getHeroCtaUrl(), e.getFooterText()); }
    public SiteSettingsPublicDto publicDto(SiteSettings e) { return new SiteSettingsPublicDto(e.getId(), e.getVersion(), e.getChurchName(), e.getAddress(), e.getLatitude(), e.getLongitude(), e.getEmail(), e.getPhone(), e.getSocials(), e.getHeroHeadline(), e.getHeroImage() == null ? null : e.getHeroImage().getStorageKey(), e.getHeroCtaText(), e.getHeroCtaUrl(), e.getFooterText()); }
}
