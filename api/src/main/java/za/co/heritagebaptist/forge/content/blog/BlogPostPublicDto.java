package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;

public record BlogPostPublicDto(
    UUID id,
    long version,
    String title,
    String slug,
    String author,
    String excerpt,
    String body,
    String coverImageKey,
    List<String> tags,
    Instant publishedAt) {}
