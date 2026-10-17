package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;

public record EventPublicDto(
    UUID id,
    long version,
    String title,
    String slug,
    Instant startsAt,
    Instant endsAt,
    boolean allDay,
    String location,
    String description,
    String coverKey,
    String recurrence,
    LocalDate recurrenceUntil,
    String ministryKey) {}
