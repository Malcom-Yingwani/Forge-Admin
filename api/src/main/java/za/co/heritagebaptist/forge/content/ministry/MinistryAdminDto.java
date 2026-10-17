package za.co.heritagebaptist.forge.content.ministry;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record MinistryAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, String slug, String description, String meetingInfo, String contact, boolean contactPublic, UUID imageId, int sortOrder, boolean published) {}
