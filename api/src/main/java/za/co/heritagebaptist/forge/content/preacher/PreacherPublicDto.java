package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;

public record PreacherPublicDto(
    UUID id,
    long version,
    String name,
    String slug,
    String title,
    String bio,
    String photoKey) {}
