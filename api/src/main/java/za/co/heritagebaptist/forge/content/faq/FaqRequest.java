package za.co.heritagebaptist.forge.content.faq;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

public record FaqRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=200) String question,
    @NotBlank @Size(max=200000) String answer,
    int sortOrder,
    boolean published) {}
