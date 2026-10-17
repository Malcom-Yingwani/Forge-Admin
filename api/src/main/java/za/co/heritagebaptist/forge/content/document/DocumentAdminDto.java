package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.document.DocumentCategory;

public record DocumentAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String title, String slug, DocumentCategory category, Integer year, String origin, String summary, String body, UUID pdfId, int sortOrder, boolean published) {}
