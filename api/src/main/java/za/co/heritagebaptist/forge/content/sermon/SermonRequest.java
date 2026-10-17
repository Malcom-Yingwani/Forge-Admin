package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.sermon.ScriptureBook;
import za.co.heritagebaptist.forge.content.sermon.ServiceType;

public record SermonRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String title,
    @Size(max=255) String slug,
    @NotNull LocalDate preachedOn,
    @NotNull ServiceType service,
    @NotNull UUID preacherId,
    UUID seriesId,
    ScriptureBook scriptureBook,
    @Size(max=255) String scriptureRef,
    @Size(max=200000) String summary,
    UUID audioId,
    @Size(max=200000) String videoUrl,
    @PositiveOrZero Integer durationSeconds,
    boolean published) {}
