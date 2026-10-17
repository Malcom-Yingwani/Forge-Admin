package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;

public record DocumentPublicDto(
    UUID id,
    long version,
    String title,
    String slug,
    String category,
    Integer year,
    String origin,
    String summary,
    String body,
    String pdfKey,
    int sortOrder) {}
