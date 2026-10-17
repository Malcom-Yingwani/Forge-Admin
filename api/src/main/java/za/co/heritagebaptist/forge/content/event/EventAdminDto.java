package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record EventAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String title, String slug, Instant startsAt, Instant endsAt, boolean allDay, String location, String description, UUID coverId, String recurrence, LocalDate recurrenceUntil, UUID ministryId, boolean published) {}
