package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record PreacherAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, String slug, String title, String bio, UUID photoId) {}
