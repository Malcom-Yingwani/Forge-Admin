package za.co.heritagebaptist.forge.content.faq;
import java.util.*;
import java.time.*;

public record FaqPublicDto(
    UUID id,
    long version,
    String question,
    String answer,
    int sortOrder) {}
