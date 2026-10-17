package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;

public record PersonPublicDto(
    UUID id,
    long version,
    String name,
    String category,
    String office,
    String roleTitle,
    String bio,
    String photoKey,
    String publicEmail,
    int sortOrder) {}
