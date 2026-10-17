package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.event.Event;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class EventService extends ContentService<Event,EventRequest,EventAdminDto,EventPublicDto> {
    public EventService(ContentSupport support,EventRepository repository,EventMapper mapper) { super(support,repository,mapper,Event.class,"event",Set.of("id","createdAt","updatedAt","title","slug","startsAt","endsAt","allDay","location","description","recurrence","recurrenceUntil","published"),List.of("title","slug")); }
    protected Event fresh() { return new Event(); }
    protected Long version(EventRequest request) { return request.version(); }
    @Override protected void setPublished(Event e,boolean published) { e.setPublished(published); }
}
