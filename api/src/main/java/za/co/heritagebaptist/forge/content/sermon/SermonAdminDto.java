package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.sermon.ScriptureBook;
import za.co.heritagebaptist.forge.content.sermon.ServiceType;

public record SermonAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String title, String slug, LocalDate preachedOn, ServiceType service, UUID preacherId, UUID seriesId, ScriptureBook scriptureBook, String scriptureRef, String summary, UUID audioId, String videoUrl, Integer durationSeconds, boolean published) {}
