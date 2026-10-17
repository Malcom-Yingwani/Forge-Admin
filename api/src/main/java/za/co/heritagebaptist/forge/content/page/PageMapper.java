package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.page.Page;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class PageMapper implements ContentMapper<Page,PageRequest,PageAdminDto,PagePublicDto> {
    private final ContentSupport support;
    public PageMapper(ContentSupport support) { this.support=support; }
    public void apply(Page e,PageRequest r) {
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Page.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setTitle(r.title());
        e.setBody(r.body());
        e.setSection(r.section());
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public PageAdminDto admin(Page e) { return new PageAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getSlug(), e.getTitle(), e.getBody(), e.getSection(), e.getSortOrder(), e.isPublished()); }
    public PagePublicDto publicDto(Page e) { return new PagePublicDto(e.getId(), e.getVersion(), e.getSlug(), e.getTitle(), e.getBody(), e.getSection(), e.getSortOrder()); }
}
