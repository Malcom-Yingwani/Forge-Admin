package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record GivingFundAdminDto(UUID id, long version, Instant createdAt, Instant updatedAt, String name, String slug, String description, String accountName, String bank, String branchCode, String accountNumber, String accountType, String paymentReference, String snapscanUrl, UUID snapscanQrId, BigDecimal targetAmount, BigDecimal raisedAmount, boolean showProgress, int sortOrder, boolean published) {}
