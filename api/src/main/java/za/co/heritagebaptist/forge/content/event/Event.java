package za.co.heritagebaptist.forge.content.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.common.Publishable;
import za.co.heritagebaptist.forge.content.ministry.Ministry;
import za.co.heritagebaptist.forge.media.MediaAsset;

@Entity
@Table(name = "event")
public class Event extends BaseEntity implements Publishable {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "all_day", nullable = false)
    private boolean allDay;

    @Column(name = "location", columnDefinition = "text")
    private String location;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_id")
    private MediaAsset cover;

    @Column(name = "recurrence", nullable = false)
    private String recurrence = "NONE";

    @Column(name = "recurrence_until")
    private LocalDate recurrenceUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;

    @Column(name = "published", nullable = false)
    private boolean published;

    public Event() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaAsset getCover() {
        return cover;
    }

    public void setCover(MediaAsset cover) {
        this.cover = cover;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public LocalDate getRecurrenceUntil() {
        return recurrenceUntil;
    }

    public void setRecurrenceUntil(LocalDate recurrenceUntil) {
        this.recurrenceUntil = recurrenceUntil;
    }

    public Ministry getMinistry() {
        return ministry;
    }

    public void setMinistry(Ministry ministry) {
        this.ministry = ministry;
    }

    @Override
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
