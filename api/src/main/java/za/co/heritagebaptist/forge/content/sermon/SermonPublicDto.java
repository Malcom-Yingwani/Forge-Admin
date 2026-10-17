package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;

public record SermonPublicDto(
    UUID id,
    long version,
    String title,
    String slug,
    LocalDate preachedOn,
    String service,
    String scriptureBook,
    String scriptureRef,
    String summary,
    String audioKey,
    String videoUrl,
    Integer durationSeconds,
    UUID preacherId,
    String preacherName,
    UUID seriesId,
    String seriesTitle) {}
