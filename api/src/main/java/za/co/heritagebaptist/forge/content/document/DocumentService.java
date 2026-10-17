package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.document.Document;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class DocumentService extends ContentService<Document,DocumentRequest,DocumentAdminDto,DocumentPublicDto> {
    public DocumentService(ContentSupport support,DocumentRepository repository,DocumentMapper mapper) { super(support,repository,mapper,Document.class,"document",Set.of("id","createdAt","updatedAt","title","slug","category","year","origin","summary","body","sortOrder","published"),List.of("title","slug","summary")); }
    protected Document fresh() { return new Document(); }
    protected Long version(DocumentRequest request) { return request.version(); }
    @Override protected void setPublished(Document e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(Document e,int order) { e.setSortOrder(order); }
}
