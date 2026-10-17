package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;

public record GrowthGroupPublicDto(
    UUID id,
    long version,
    String name,
    String area,
    String host,
    String dayOfWeek,
    LocalTime time,
    String contact,
    boolean active) {}
