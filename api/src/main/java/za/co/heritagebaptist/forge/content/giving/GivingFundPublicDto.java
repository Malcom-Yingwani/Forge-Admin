package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public record GivingFundPublicDto(
    UUID id,
    long version,
    String name,
    String slug,
    String description,
    String accountName,
    String bank,
    String branchCode,
    String accountNumber,
    String accountType,
    String paymentReference,
    String snapscanUrl,
    String snapscanQrKey,
    BigDecimal targetAmount,
    BigDecimal raisedAmount,
    boolean showProgress,
    int sortOrder) {}
