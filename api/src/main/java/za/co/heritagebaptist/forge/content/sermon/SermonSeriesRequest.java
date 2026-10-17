package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record SermonSeriesRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String title,
    @Size(max=255) String slug,
    @Size(max=200000) String description,
    UUID coverImageId,
    LocalDate startDate,
    LocalDate endDate) {}
