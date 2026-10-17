package za.co.heritagebaptist.forge.content.sermon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import za.co.heritagebaptist.forge.common.BaseEntity;
import za.co.heritagebaptist.forge.common.Publishable;
import za.co.heritagebaptist.forge.content.preacher.Preacher;
import za.co.heritagebaptist.forge.media.MediaAsset;

@Entity
@Table(name = "sermon")
public class Sermon extends BaseEntity implements Publishable {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "preached_on", nullable = false)
    private LocalDate preachedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false)
    private ServiceType service;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preacher_id", nullable = false)
    private Preacher preacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private SermonSeries series;

    @Enumerated(EnumType.STRING)
    @Column(name = "scripture_book")
    private ScriptureBook scriptureBook;

    @Column(name = "scripture_ref")
    private String scriptureRef;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_id")
    private MediaAsset audio;

    @Column(name = "video_url", columnDefinition = "text")
    private String videoUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "published", nullable = false)
    private boolean published;

    public Sermon() {
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

    public LocalDate getPreachedOn() {
        return preachedOn;
    }

    public void setPreachedOn(LocalDate preachedOn) {
        this.preachedOn = preachedOn;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public Preacher getPreacher() {
        return preacher;
    }

    public void setPreacher(Preacher preacher) {
        this.preacher = preacher;
    }

    public SermonSeries getSeries() {
        return series;
    }

    public void setSeries(SermonSeries series) {
        this.series = series;
    }

    public ScriptureBook getScriptureBook() {
        return scriptureBook;
    }

    public void setScriptureBook(ScriptureBook scriptureBook) {
        this.scriptureBook = scriptureBook;
    }

    public String getScriptureRef() {
        return scriptureRef;
    }

    public void setScriptureRef(String scriptureRef) {
        this.scriptureRef = scriptureRef;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public MediaAsset getAudio() {
        return audio;
    }

    public void setAudio(MediaAsset audio) {
        this.audio = audio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    @Override
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
