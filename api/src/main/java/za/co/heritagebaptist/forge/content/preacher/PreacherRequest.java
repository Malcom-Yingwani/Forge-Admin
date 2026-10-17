package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record PreacherRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @Size(max=255) String slug,
    @Size(max=255) String title,
    @Size(max=200000) String bio,
    UUID photoId) {}
