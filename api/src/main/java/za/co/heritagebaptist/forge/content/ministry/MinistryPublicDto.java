package za.co.heritagebaptist.forge.content.ministry;
import java.util.*;
import java.time.*;

public record MinistryPublicDto(
    UUID id,
    long version,
    String name,
    String slug,
    String description,
    String meetingInfo,
    String contact,
    String imageKey,
    int sortOrder) {}
