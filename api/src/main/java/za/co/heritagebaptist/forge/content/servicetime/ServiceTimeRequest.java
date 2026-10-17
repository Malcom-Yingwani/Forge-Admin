package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record ServiceTimeRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @NotNull DayOfWeek dayOfWeek,
    @NotNull LocalTime time,
    @Size(max=200000) String notes,
    boolean active) {}
