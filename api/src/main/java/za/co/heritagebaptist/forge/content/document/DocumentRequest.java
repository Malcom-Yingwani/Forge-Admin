package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.document.DocumentCategory;

public record DocumentRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String title,
    @Size(max=255) String slug,
    @NotNull DocumentCategory category,
    Integer year,
    @Size(max=255) String origin,
    @Size(max=300) String summary,
    @Size(max=200000) String body,
    UUID pdfId,
    int sortOrder,
    boolean published) {}
