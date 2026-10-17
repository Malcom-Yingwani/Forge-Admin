package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record BlogPostRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String title,
    @Size(max=255) String slug,
    UUID authorId,
    @Size(max=300) String excerpt,
    @NotBlank @Size(max=200000) String body,
    UUID coverImageId,
    List<String> tags,
    Instant publishedAt,
    boolean published) {}
