package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;

public record PagePublicDto(
    UUID id,
    long version,
    String slug,
    String title,
    String body,
    String section,
    int sortOrder) {}
