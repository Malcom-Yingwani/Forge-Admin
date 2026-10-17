package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record BlogPostAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String title, String slug, UUID authorId, String excerpt, String body, UUID coverImageId, List<String> tags, Instant publishedAt, boolean published) {}
