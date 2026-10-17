package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record PageRequest(
    @PositiveOrZero Long version,
    @Size(max=255) String slug,
    @NotBlank @Size(max=255) String title,
    @NotBlank @Size(max=200000) String body,
    @NotBlank @Size(max=255) @Pattern(regexp="ABOUT|RESOURCES|CONTACT|OTHER") String section,
    int sortOrder,
    boolean published) {}
