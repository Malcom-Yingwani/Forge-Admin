package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.preacher.Preacher;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class PreacherMapper implements ContentMapper<Preacher,PreacherRequest,PreacherAdminDto,PreacherPublicDto> {
    private final ContentSupport support;
    public PreacherMapper(ContentSupport support) { this.support=support; }
    public void apply(Preacher e,PreacherRequest r) {
        e.setName(r.name());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Preacher.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setTitle(r.title());
        e.setBio(r.bio());
        e.setPhoto(support.reference(MediaAsset.class,r.photoId()));
    }
    public PreacherAdminDto admin(Preacher e) { return new PreacherAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getSlug(), e.getTitle(), e.getBio(), e.getPhoto() == null ? null : e.getPhoto().getId()); }
    public PreacherPublicDto publicDto(Preacher e) { return new PreacherPublicDto(e.getId(), e.getVersion(), e.getName(), e.getSlug(), e.getTitle(), e.getBio(), e.getPhoto() == null ? null : e.getPhoto().getStorageKey()); }
}
