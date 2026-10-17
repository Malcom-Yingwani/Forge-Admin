package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.event.Event;
import za.co.heritagebaptist.forge.content.ministry.Ministry;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class EventMapper implements ContentMapper<Event,EventRequest,EventAdminDto,EventPublicDto> {
    private final ContentSupport support;
    public EventMapper(ContentSupport support) { this.support=support; }
    public void apply(Event e,EventRequest r) {
        e.setTitle(r.title());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Event.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setStartsAt(r.startsAt());
        e.setEndsAt(r.endsAt());
        e.setAllDay(r.allDay());
        e.setLocation(r.location());
        e.setDescription(r.description());
        e.setCover(support.reference(MediaAsset.class,r.coverId()));
        e.setRecurrence(r.recurrence());
        e.setRecurrenceUntil(r.recurrenceUntil());
        e.setMinistry(support.reference(Ministry.class,r.ministryId()));
        e.setPublished(r.published());
    }
    public EventAdminDto admin(Event e) { return new EventAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getTitle(), e.getSlug(), e.getStartsAt(), e.getEndsAt(), e.isAllDay(), e.getLocation(), e.getDescription(), e.getCover() == null ? null : e.getCover().getId(), e.getRecurrence(), e.getRecurrenceUntil(), e.getMinistry() == null ? null : e.getMinistry().getId(), e.isPublished()); }
    public EventPublicDto publicDto(Event e) { return new EventPublicDto(e.getId(), e.getVersion(), e.getTitle(), e.getSlug(), e.getStartsAt(), e.getEndsAt(), e.isAllDay(), e.getLocation(), e.getDescription(), e.getCover() == null ? null : e.getCover().getStorageKey(), e.getRecurrence(), e.getRecurrenceUntil(), e.getMinistry() == null ? null : e.getMinistry().getSlug()); }
}
