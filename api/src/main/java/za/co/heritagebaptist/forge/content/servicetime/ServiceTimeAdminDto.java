package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record ServiceTimeAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, DayOfWeek dayOfWeek, LocalTime time, String notes, boolean active) {}
