package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.servicetime.ServiceTime;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class ServiceTimeMapper implements ContentMapper<ServiceTime,ServiceTimeRequest,ServiceTimeAdminDto,ServiceTimePublicDto> {
    private final ContentSupport support;
    public ServiceTimeMapper(ContentSupport support) { this.support=support; }
    public void apply(ServiceTime e,ServiceTimeRequest r) {
        e.setName(r.name());
        e.setDayOfWeek(r.dayOfWeek());
        e.setTime(r.time());
        e.setNotes(r.notes());
        e.setActive(r.active());
    }
    public ServiceTimeAdminDto admin(ServiceTime e) { return new ServiceTimeAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getDayOfWeek(), e.getTime(), e.getNotes(), e.isActive()); }
    public ServiceTimePublicDto publicDto(ServiceTime e) { return new ServiceTimePublicDto(e.getId(), e.getVersion(), e.getName(), e.getDayOfWeek() == null ? null : e.getDayOfWeek().name(), e.getTime(), e.getNotes(), e.isActive()); }
}
