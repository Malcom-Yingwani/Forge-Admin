package za.co.heritagebaptist.forge.content.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.media.MediaAsset;

@Entity
@Table(name = "site_settings")
public class SiteSettings extends BaseEntity {
    @Column(name = "church_name", nullable = false)
    private String churchName;

    @Column(name = "address", nullable = false, columnDefinition = "text")
    private String address;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "socials", columnDefinition = "jsonb")
    private Map<String, Object> socials;

    @Column(name = "hero_headline")
    private String heroHeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_image_id")
    private MediaAsset heroImage;

    @Column(name = "hero_cta_text")
    private String heroCtaText;

    @Column(name = "hero_cta_url", columnDefinition = "text")
    private String heroCtaUrl;

    @Column(name = "footer_text", columnDefinition = "text")
    private String footerText;

    public SiteSettings() {
    }

    public String getChurchName() {
        return churchName;
    }

    public void setChurchName(String churchName) {
        this.churchName = churchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Map<String, Object> getSocials() {
        return socials;
    }

    public void setSocials(Map<String, Object> socials) {
        this.socials = socials;
    }

    public String getHeroHeadline() {
        return heroHeadline;
    }

    public void setHeroHeadline(String heroHeadline) {
        this.heroHeadline = heroHeadline;
    }

    public MediaAsset getHeroImage() {
        return heroImage;
    }

    public void setHeroImage(MediaAsset heroImage) {
        this.heroImage = heroImage;
    }

    public String getHeroCtaText() {
        return heroCtaText;
    }

    public void setHeroCtaText(String heroCtaText) {
        this.heroCtaText = heroCtaText;
    }

    public String getHeroCtaUrl() {
        return heroCtaUrl;
    }

    public void setHeroCtaUrl(String heroCtaUrl) {
        this.heroCtaUrl = heroCtaUrl;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }
}
