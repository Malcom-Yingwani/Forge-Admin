package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record SermonSeriesAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String title, String slug, String description, UUID coverImageId, LocalDate startDate, LocalDate endDate) {}
