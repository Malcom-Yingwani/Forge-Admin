package za.co.heritagebaptist.forge.content.ministry;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record MinistryRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @Size(max=255) String slug,
    @Size(max=200000) String description,
    @Size(max=200000) String meetingInfo,
    @Size(max=200000) String contact,
    boolean contactPublic,
    UUID imageId,
    int sortOrder,
    boolean published) {}
