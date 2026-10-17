package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.page.Page;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class PageService extends ContentService<Page,PageRequest,PageAdminDto,PagePublicDto> {
    public PageService(ContentSupport support,PageRepository repository,PageMapper mapper) { super(support,repository,mapper,Page.class,"page",Set.of("id","createdAt","updatedAt","slug","title","body","section","sortOrder","published"),List.of("slug","title")); }
    protected Page fresh() { return new Page(); }
    protected Long version(PageRequest request) { return request.version(); }
    @Override protected void setPublished(Page e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(Page e,int order) { e.setSortOrder(order); }
}
