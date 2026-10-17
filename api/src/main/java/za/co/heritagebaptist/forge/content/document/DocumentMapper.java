package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.document.Document;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class DocumentMapper implements ContentMapper<Document,DocumentRequest,DocumentAdminDto,DocumentPublicDto> {
    private final ContentSupport support;
    public DocumentMapper(ContentSupport support) { this.support=support; }
    public void apply(Document e,DocumentRequest r) {
        e.setTitle(r.title());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Document.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setCategory(r.category());
        e.setYear(r.year());
        e.setOrigin(r.origin());
        e.setSummary(r.summary());
        e.setBody(r.body());
        e.setPdf(support.reference(MediaAsset.class,r.pdfId()));
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public DocumentAdminDto admin(Document e) { return new DocumentAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getTitle(), e.getSlug(), e.getCategory(), e.getYear(), e.getOrigin(), e.getSummary(), e.getBody(), e.getPdf() == null ? null : e.getPdf().getId(), e.getSortOrder(), e.isPublished()); }
    public DocumentPublicDto publicDto(Document e) { return new DocumentPublicDto(e.getId(), e.getVersion(), e.getTitle(), e.getSlug(), e.getCategory() == null ? null : e.getCategory().name(), e.getYear(), e.getOrigin(), e.getSummary(), e.getBody(), e.getPdf() == null ? null : e.getPdf().getStorageKey(), e.getSortOrder()); }
}
