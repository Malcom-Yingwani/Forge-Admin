package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record EventRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String title,
    @Size(max=255) String slug,
    @NotNull Instant startsAt,
    Instant endsAt,
    boolean allDay,
    @Size(max=200000) String location,
    @Size(max=200000) String description,
    UUID coverId,
    @NotBlank @Size(max=255) @Pattern(regexp="NONE|WEEKLY|MONTHLY") String recurrence,
    LocalDate recurrenceUntil,
    UUID ministryId,
    boolean published) {}
