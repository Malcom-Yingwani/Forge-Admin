package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.person.Person;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class PersonMapper implements ContentMapper<Person,PersonRequest,PersonAdminDto,PersonPublicDto> {
    private final ContentSupport support;
    public PersonMapper(ContentSupport support) { this.support=support; }
    public void apply(Person e,PersonRequest r) {
        e.setName(r.name());
        e.setCategory(r.category());
        e.setOffice(r.office());
        e.setRoleTitle(r.roleTitle());
        e.setBio(r.bio());
        e.setPhoto(support.reference(MediaAsset.class,r.photoId()));
        e.setPublicEmail(r.publicEmail());
        if(!java.util.Objects.equals(e.getConsentAt(),r.consentAt())) e.setConsentBy(r.consentAt()==null?null:support.actor()); e.setConsentAt(r.consentAt());
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public PersonAdminDto admin(Person e) { return new PersonAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getCategory(), e.getOffice(), e.getRoleTitle(), e.getBio(), e.getPhoto() == null ? null : e.getPhoto().getId(), e.getPublicEmail(), e.getConsentAt(), e.getConsentBy() == null ? null : e.getConsentBy().getId(), e.getSortOrder(), e.isPublished()); }
    public PersonPublicDto publicDto(Person e) { return new PersonPublicDto(e.getId(), e.getVersion(), e.getName(), e.getCategory(), e.getOffice() == null ? null : e.getOffice().name(), e.getRoleTitle(), e.getBio(), e.getPhoto() == null ? null : e.getPhoto().getStorageKey(), e.getPublicEmail(), e.getSortOrder()); }
}
