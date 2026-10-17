package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.servicetime.ServiceTime;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class ServiceTimeService extends ContentService<ServiceTime,ServiceTimeRequest,ServiceTimeAdminDto,ServiceTimePublicDto> {
    public ServiceTimeService(ContentSupport support,ServiceTimeRepository repository,ServiceTimeMapper mapper) { super(support,repository,mapper,ServiceTime.class,"service-time",Set.of("id","createdAt","updatedAt","name","dayOfWeek","time","notes","active"),List.of("name")); }
    protected ServiceTime fresh() { return new ServiceTime(); }
    protected Long version(ServiceTimeRequest request) { return request.version(); }
}
