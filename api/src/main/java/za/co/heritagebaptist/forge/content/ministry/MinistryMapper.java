package za.co.heritagebaptist.forge.content.ministry;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.ministry.Ministry;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class MinistryMapper implements ContentMapper<Ministry,MinistryRequest,MinistryAdminDto,MinistryPublicDto> {
    private final ContentSupport support;
    public MinistryMapper(ContentSupport support) { this.support=support; }
    public void apply(Ministry e,MinistryRequest r) {
        e.setName(r.name());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Ministry.class,r.slug()==null||r.slug().isBlank()?r.name():r.slug(),e.getId()));
        e.setDescription(r.description());
        e.setMeetingInfo(r.meetingInfo());
        e.setContact(r.contact());
        e.setContactPublic(r.contactPublic());
        e.setImage(support.reference(MediaAsset.class,r.imageId()));
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public MinistryAdminDto admin(Ministry e) { return new MinistryAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getSlug(), e.getDescription(), e.getMeetingInfo(), e.getContact(), e.isContactPublic(), e.getImage() == null ? null : e.getImage().getId(), e.getSortOrder(), e.isPublished()); }
    public MinistryPublicDto publicDto(Ministry e) { return new MinistryPublicDto(e.getId(), e.getVersion(), e.getName(), e.getSlug(), e.getDescription(), e.getMeetingInfo(), e.isContactPublic() ? e.getContact() : null, e.getImage() == null ? null : e.getImage().getStorageKey(), e.getSortOrder()); }
}
