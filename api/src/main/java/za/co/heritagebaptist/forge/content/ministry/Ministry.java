package za.co.heritagebaptist.forge.content.ministry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.common.Publishable;
import za.co.heritagebaptist.forge.media.MediaAsset;

@Entity
@Table(name = "ministry")
public class Ministry extends BaseEntity implements Publishable {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "meeting_info", columnDefinition = "text")
    private String meetingInfo;

    @Column(name = "contact", columnDefinition = "text")
    private String contact;

    @Column(name = "contact_public", nullable = false)
    private boolean contactPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private MediaAsset image;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "published", nullable = false)
    private boolean published;

    public Ministry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(String meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isContactPublic() {
        return contactPublic;
    }

    public void setContactPublic(boolean contactPublic) {
        this.contactPublic = contactPublic;
    }

    public MediaAsset getImage() {
        return image;
    }

    public void setImage(MediaAsset image) {
        this.image = image;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
