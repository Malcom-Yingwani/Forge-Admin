package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record PageAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String slug, String title, String body, String section, int sortOrder, boolean published) {}
