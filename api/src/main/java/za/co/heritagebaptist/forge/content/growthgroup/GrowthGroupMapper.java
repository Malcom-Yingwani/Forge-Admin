package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.growthgroup.GrowthGroup;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class GrowthGroupMapper implements ContentMapper<GrowthGroup,GrowthGroupRequest,GrowthGroupAdminDto,GrowthGroupPublicDto> {
    private final ContentSupport support;
    public GrowthGroupMapper(ContentSupport support) { this.support=support; }
    public void apply(GrowthGroup e,GrowthGroupRequest r) {
        e.setName(r.name());
        e.setArea(r.area());
        e.setHost(r.host());
        e.setDayOfWeek(r.dayOfWeek());
        e.setTime(r.time());
        e.setContact(r.contact());
        e.setContactPublic(r.contactPublic());
        e.setActive(r.active());
    }
    public GrowthGroupAdminDto admin(GrowthGroup e) { return new GrowthGroupAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getArea(), e.getHost(), e.getDayOfWeek(), e.getTime(), e.getContact(), e.isContactPublic(), e.isActive()); }
    public GrowthGroupPublicDto publicDto(GrowthGroup e) { return new GrowthGroupPublicDto(e.getId(), e.getVersion(), e.getName(), e.getArea(), e.getHost(), e.getDayOfWeek() == null ? null : e.getDayOfWeek().name(), e.getTime(), e.isContactPublic() ? e.getContact() : null, e.isActive()); }
}
