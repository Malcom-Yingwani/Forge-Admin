package za.co.heritagebaptist.forge.content.ministry;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.ministry.Ministry;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class MinistryService extends ContentService<Ministry,MinistryRequest,MinistryAdminDto,MinistryPublicDto> {
    public MinistryService(ContentSupport support,MinistryRepository repository,MinistryMapper mapper) { super(support,repository,mapper,Ministry.class,"ministry",Set.of("id","createdAt","updatedAt","name","slug","description","meetingInfo","contact","contactPublic","sortOrder","published"),List.of("name","slug")); }
    protected Ministry fresh() { return new Ministry(); }
    protected Long version(MinistryRequest request) { return request.version(); }
    @Override protected void setPublished(Ministry e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(Ministry e,int order) { e.setSortOrder(order); }
}
