package za.co.heritagebaptist.forge.content.preacher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.media.MediaAsset;

@Entity
@Table(name = "preacher")
public class Preacher extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title")
    private String title;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private MediaAsset photo;

    public Preacher() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public MediaAsset getPhoto() {
        return photo;
    }

    public void setPhoto(MediaAsset photo) {
        this.photo = photo;
    }
}
