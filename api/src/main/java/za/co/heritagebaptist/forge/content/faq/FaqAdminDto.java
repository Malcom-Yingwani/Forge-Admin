package za.co.heritagebaptist.forge.content.faq;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record FaqAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String question, String answer, int sortOrder, boolean published) {}
