package za.co.heritagebaptist.forge.content.settings;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.settings.SiteSettings;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class SiteSettingsService extends ContentService<SiteSettings,SiteSettingsRequest,SiteSettingsAdminDto,SiteSettingsPublicDto> {
    public SiteSettingsService(ContentSupport support,SiteSettingsRepository repository,SiteSettingsMapper mapper) { super(support,repository,mapper,SiteSettings.class,"site-settings",Set.of("id","createdAt","updatedAt","churchName","address","latitude","longitude","email","phone","heroHeadline","heroCtaText","heroCtaUrl","footerText"),List.of("churchName")); }
    protected SiteSettings fresh() { return new SiteSettings(); }
    protected Long version(SiteSettingsRequest request) { return request.version(); }
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public SiteSettingsAdminDto singleton() {
        support.em.createNativeQuery("select 1 from pg_advisory_xact_lock(70198)",Integer.class).getSingleResult();
        var rows=repository.findAll();
        if(!rows.isEmpty()) return mapper.admin(rows.getFirst());
        var e=new SiteSettings();e.setChurchName("Heritage Baptist Church");e.setAddress("41 7th Avenue, Melville, Johannesburg");repository.saveAndFlush(e);
        var dto=mapper.admin(e);support.changed("site-settings",e,"CREATE",Map.of(),dto);return dto;
    }
}
