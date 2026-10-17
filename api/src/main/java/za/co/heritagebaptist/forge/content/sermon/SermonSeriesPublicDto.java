package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;

public record SermonSeriesPublicDto(
    UUID id,
    long version,
    String title,
    String slug,
    String description,
    String coverImageKey,
    LocalDate startDate,
    LocalDate endDate) {}
