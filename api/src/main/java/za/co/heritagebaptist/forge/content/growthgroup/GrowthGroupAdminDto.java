package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record GrowthGroupAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, String area, String host, DayOfWeek dayOfWeek, LocalTime time, String contact, boolean contactPublic, boolean active) {}
