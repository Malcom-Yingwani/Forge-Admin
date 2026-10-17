package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.media.MediaAsset;
import za.co.heritagebaptist.forge.content.sermon.SermonSeries;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Component
public class SermonSeriesMapper implements ContentMapper<SermonSeries,SermonSeriesRequest,SermonSeriesAdminDto,SermonSeriesPublicDto> {
    private final ContentSupport support;
    public SermonSeriesMapper(ContentSupport support) { this.support=support; }
    public void apply(SermonSeries e,SermonSeriesRequest r) {
        e.setTitle(r.title());
        if(r.slug()!=null || e.getSlug()==null) e.setSlug(support.slug(SermonSeries.class,r.slug()==null||r.slug().isBlank()?r.title():r.slug(),e.getId()));
        e.setDescription(r.description());
        e.setCoverImage(support.reference(MediaAsset.class,r.coverImageId()));
        e.setStartDate(r.startDate());
        e.setEndDate(r.endDate());
    }
    public SermonSeriesAdminDto admin(SermonSeries e) { return new SermonSeriesAdminDto(e.getId(), e.getVersion(), e.getCreatedAt(), e.getUpdatedAt(), e.getTitle(), e.getSlug(), e.getDescription(), e.getCoverImage() == null ? null : e.getCoverImage().getId(), e.getStartDate(), e.getEndDate()); }
    public SermonSeriesPublicDto publicDto(SermonSeries e) { return new SermonSeriesPublicDto(e.getId(), e.getVersion(), e.getTitle(), e.getSlug(), e.getDescription(), e.getCoverImage() == null ? null : e.getCoverImage().getStorageKey(), e.getStartDate(), e.getEndDate()); }
}
