package za.co.heritagebaptist.forge.content.faq;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.faq.Faq;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class FaqService extends ContentService<Faq,FaqRequest,FaqAdminDto,FaqPublicDto> {
    public FaqService(ContentSupport support,FaqRepository repository,FaqMapper mapper) { super(support,repository,mapper,Faq.class,"faq",Set.of("id","createdAt","updatedAt","question","answer","sortOrder","published"),List.of("question")); }
    protected Faq fresh() { return new Faq(); }
    protected Long version(FaqRequest request) { return request.version(); }
    @Override protected void setPublished(Faq e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(Faq e,int order) { e.setSortOrder(order); }
}
