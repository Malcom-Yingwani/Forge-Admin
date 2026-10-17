package za.co.heritagebaptist.forge.content.faq;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.faq.Faq;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class FaqMapper implements ContentMapper<Faq,FaqRequest,FaqAdminDto,FaqPublicDto> {
    private final ContentSupport support;
    public FaqMapper(ContentSupport support) { this.support=support; }
    public void apply(Faq e,FaqRequest r) {
        e.setQuestion(r.question());
        e.setAnswer(r.answer());
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public FaqAdminDto admin(Faq e) { return new FaqAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getQuestion(), e.getAnswer(), e.getSortOrder(), e.isPublished()); }
    public FaqPublicDto publicDto(Faq e) { return new FaqPublicDto(e.getId(), e.getVersion(), e.getQuestion(), e.getAnswer(), e.getSortOrder()); }
}
