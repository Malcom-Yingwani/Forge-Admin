package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record GivingFundRequest(
    @PositiveOrZero Long version,
    @NotBlank @Size(max=255) String name,
    @Size(max=255) String slug,
    @Size(max=200000) String description,
    @NotBlank @Size(max=255) String accountName,
    @NotBlank @Size(max=255) String bank,
    @NotBlank @Size(max=255) @Pattern(regexp="[0-9]+") String branchCode,
    @NotBlank @Size(max=255) @Pattern(regexp="[0-9]+") String accountNumber,
    @NotBlank @Size(max=255) String accountType,
    @Size(max=255) String paymentReference,
    @Size(max=200000) String snapscanUrl,
    UUID snapscanQrId,
    @PositiveOrZero BigDecimal targetAmount,
    @PositiveOrZero BigDecimal raisedAmount,
    boolean showProgress,
    int sortOrder,
    boolean published) {}
