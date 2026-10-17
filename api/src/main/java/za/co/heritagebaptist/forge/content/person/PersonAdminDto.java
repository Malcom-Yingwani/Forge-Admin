package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.person.Office;

public record PersonAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, String category, Office office, String roleTitle, String bio, UUID photoId, String publicEmail, Instant consentAt, UUID consentById, int sortOrder, boolean published) {}
