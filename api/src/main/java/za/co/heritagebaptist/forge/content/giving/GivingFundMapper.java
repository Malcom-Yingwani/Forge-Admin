package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.giving.GivingFund;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class GivingFundMapper implements ContentMapper<GivingFund,GivingFundRequest,GivingFundAdminDto,GivingFundPublicDto> {
    private final ContentSupport support;
    public GivingFundMapper(ContentSupport support) { this.support=support; }
    public void apply(GivingFund e,GivingFundRequest r) {
        e.setName(r.name());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(GivingFund.class,r.slug()==null||r.slug().isBlank()?r.name():r.slug(),e.getId()));
        e.setDescription(r.description());
        e.setAccountName(r.accountName());
        e.setBank(r.bank());
        e.setBranchCode(r.branchCode());
        e.setAccountNumber(r.accountNumber());
        e.setAccountType(r.accountType());
        e.setPaymentReference(r.paymentReference());
        e.setSnapscanUrl(r.snapscanUrl());
        e.setSnapscanQr(support.reference(MediaAsset.class,r.snapscanQrId()));
        e.setTargetAmount(r.targetAmount());
        e.setRaisedAmount(r.raisedAmount());
        e.setShowProgress(r.showProgress());
        e.setSortOrder(r.sortOrder());
        e.setPublished(r.published());
    }
    public GivingFundAdminDto admin(GivingFund e) { return new GivingFundAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getName(), e.getSlug(), e.getDescription(), e.getAccountName(), e.getBank(), e.getBranchCode(), e.getAccountNumber(), e.getAccountType(), e.getPaymentReference(), e.getSnapscanUrl(), e.getSnapscanQr() == null ? null : e.getSnapscanQr().getId(), e.getTargetAmount(), e.getRaisedAmount(), e.isShowProgress(), e.getSortOrder(), e.isPublished()); }
    public GivingFundPublicDto publicDto(GivingFund e) { return new GivingFundPublicDto(e.getId(), e.getVersion(), e.getName(), e.getSlug(), e.getDescription(), e.getAccountName(), e.getBank(), e.getBranchCode(), e.getAccountNumber(), e.getAccountType(), e.getPaymentReference(), e.getSnapscanUrl(), e.getSnapscanQr() == null ? null : e.getSnapscanQr().getStorageKey(), e.getTargetAmount(), e.getRaisedAmount(), e.isShowProgress(), e.getSortOrder()); }
}
