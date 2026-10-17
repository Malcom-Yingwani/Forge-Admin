package za.co.heritagebaptist.forge.content.giving;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.giving.GivingFund;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class GivingFundService extends ContentService<GivingFund,GivingFundRequest,GivingFundAdminDto,GivingFundPublicDto> {
    public GivingFundService(ContentSupport support,GivingFundRepository repository,GivingFundMapper mapper) { super(support,repository,mapper,GivingFund.class,"giving-fund",Set.of("id","createdAt","updatedAt","name","slug","description","accountName","bank","branchCode","accountNumber","accountType","paymentReference","snapscanUrl","targetAmount","raisedAmount","showProgress","sortOrder","published"),List.of("name","slug")); }
    protected GivingFund fresh() { return new GivingFund(); }
    protected Long version(GivingFundRequest request) { return request.version(); }
    @Override protected void setPublished(GivingFund e,boolean published) { e.setPublished(published); }
    @Override protected void setOrder(GivingFund e,int order) { e.setSortOrder(order); }
    @Override @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional public GivingFundAdminDto create(GivingFundRequest request) { return super.create(request); }
    @Override @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional public GivingFundAdminDto update(UUID id,GivingFundRequest request) { return super.update(id,request); }
    @Override @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional public void delete(UUID id) { super.delete(id); }
    @Override @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional public GivingFundAdminDto publish(UUID id,boolean published) { return super.publish(id,published); }
    @Override @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional public void order(List<UUID> ids) { super.order(ids); }
}
