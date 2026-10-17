package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.preacher.Preacher;
import za.co.heritagebaptist.forge.content.sermon.Sermon;
import za.co.heritagebaptist.forge.content.sermon.SermonSeries;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class SermonMapper implements ContentMapper<Sermon,SermonRequest,SermonAdminDto,SermonPublicDto> {
    private final ContentSupport support;
    public SermonMapper(ContentSupport support) { this.support=support; }
    public void apply(Sermon e,SermonRequest r) {
        e.setTitle(r.title());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(Sermon.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setPreachedOn(r.preachedOn());
        e.setService(r.service());
        e.setPreacher(support.reference(Preacher.class,r.preacherId()));
        e.setSeries(support.reference(SermonSeries.class,r.seriesId()));
        e.setScriptureBook(r.scriptureBook());
        e.setScriptureRef(r.scriptureRef());
        e.setSummary(r.summary());
        e.setAudio(support.reference(MediaAsset.class,r.audioId()));
        e.setVideoUrl(r.videoUrl());
        e.setDurationSeconds(r.durationSeconds());
        e.setPublished(r.published());
    }
    public SermonAdminDto admin(Sermon e) { return new SermonAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getTitle(), e.getSlug(), e.getPreachedOn(), e.getService(), e.getPreacher() == null ? null : e.getPreacher().getId(), e.getSeries() == null ? null : e.getSeries().getId(), e.getScriptureBook(), e.getScriptureRef(), e.getSummary(), e.getAudio() == null ? null : e.getAudio().getId(), e.getVideoUrl(), e.getDurationSeconds(), e.isPublished()); }
    public SermonPublicDto publicDto(Sermon e) { return new SermonPublicDto(e.getId(), e.getVersion(), e.getTitle(), e.getSlug(), e.getPreachedOn(), e.getService() == null ? null : e.getService().name(), e.getScriptureBook() == null ? null : e.getScriptureBook().name(), e.getScriptureRef(), e.getSummary(), e.getAudio() == null ? null : e.getAudio().getStorageKey(), e.getVideoUrl(), e.getDurationSeconds(), e.getPreacher() == null ? null : e.getPreacher().getId(), e.getPreacher().getName(), e.getSeries() == null ? null : e.getSeries().getId(), e.getSeries() == null ? null : e.getSeries().getTitle()); }
}
