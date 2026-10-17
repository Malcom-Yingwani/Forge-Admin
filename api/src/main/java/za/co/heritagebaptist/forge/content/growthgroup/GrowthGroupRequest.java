package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record GrowthGroupRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @Size(max=255) String area,
    @Size(max=255) String host,
    @NotNull DayOfWeek dayOfWeek,
    @NotNull LocalTime time,
    @Size(max=200000) String contact,
    boolean contactPublic,
    boolean active) {}
