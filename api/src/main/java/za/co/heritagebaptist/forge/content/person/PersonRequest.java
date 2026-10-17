package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.person.Office;

public record PersonRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @NotBlank @Size(max=255) @Pattern(regexp="LEADERSHIP|OFFICE_STAFF") String category,
    Office office,
    @Size(max=255) String roleTitle,
    @Size(max=200000) String bio,
    UUID photoId,
    @Size(max=255) @Email String publicEmail,
    Instant consentAt,
    int sortOrder,
    boolean published) {}
