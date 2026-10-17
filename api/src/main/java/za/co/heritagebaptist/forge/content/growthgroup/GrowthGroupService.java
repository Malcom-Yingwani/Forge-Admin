package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.growthgroup.GrowthGroup;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class GrowthGroupService extends ContentService<GrowthGroup,GrowthGroupRequest,GrowthGroupAdminDto,GrowthGroupPublicDto> {
    public GrowthGroupService(ContentSupport support,GrowthGroupRepository repository,GrowthGroupMapper mapper) { super(support,repository,mapper,GrowthGroup.class,"growth-group",Set.of("id","createdAt","updatedAt","name","area","host","dayOfWeek","time","contact","contactPublic","active"),List.of("name")); }
    protected GrowthGroup fresh() { return new GrowthGroup(); }
    protected Long version(GrowthGroupRequest request) { return request.version(); }
}
