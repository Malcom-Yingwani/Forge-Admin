package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.preacher.Preacher;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class PreacherService extends ContentService<Preacher,PreacherRequest,PreacherAdminDto,PreacherPublicDto> {
    public PreacherService(ContentSupport support,PreacherRepository repository,PreacherMapper mapper) { super(support,repository,mapper,Preacher.class,"preacher",Set.of("id","createdAt","updatedAt","name","slug","title","bio"),List.of("name","slug","title")); }
    protected Preacher fresh() { return new Preacher(); }
    protected Long version(PreacherRequest request) { return request.version(); }
}
