package za.co.heritagebaptist.forge.issue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import za.co.heritagebaptist.forge.auth.AppUser;
import za.co.heritagebaptist.forge.common.BaseEntity;

@Entity
@Table(name = "issue_report")
public class IssueReport extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private AppUser reporter;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "page_url", columnDefinition = "text")
    private String pageUrl;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @Column(name = "status", nullable = false)
    private String status = "OPEN";

    public IssueReport() {
    }

    public AppUser getReporter() {
        return reporter;
    }

    public void setReporter(AppUser reporter) {
        this.reporter = reporter;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
