package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.sermon.Sermon;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class SermonService extends ContentService<Sermon,SermonRequest,SermonAdminDto,SermonPublicDto> {
    public SermonService(ContentSupport support,SermonRepository repository,SermonMapper mapper) { super(support,repository,mapper,Sermon.class,"sermon",Set.of("id","createdAt","updatedAt","title","slug","preachedOn","service","scriptureBook","scriptureRef","summary","videoUrl","durationSeconds","published"),List.of("title","slug","summary")); }
    protected Sermon fresh() { return new Sermon(); }
    protected Long version(SermonRequest request) { return request.version(); }
    @Override protected void setPublished(Sermon e,boolean published) { e.setPublished(published); }
}
