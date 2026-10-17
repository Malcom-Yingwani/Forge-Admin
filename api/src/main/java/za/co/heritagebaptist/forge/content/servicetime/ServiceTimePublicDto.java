package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;

public record ServiceTimePublicDto(
    UUID id,
    long version,
    String name,
    String dayOfWeek,
    LocalTime time,
    String notes,
    boolean active) {}
