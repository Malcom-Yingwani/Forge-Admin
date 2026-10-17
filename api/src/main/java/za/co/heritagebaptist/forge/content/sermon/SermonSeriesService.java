package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;
import za.co.heritagebaptist.forge.content.sermon.SermonSeries;

import za.co.heritagebaptist.forge.content.admin.*;
@org.springframework.stereotype.Service
public class SermonSeriesService extends ContentService<SermonSeries,SermonSeriesRequest,SermonSeriesAdminDto,SermonSeriesPublicDto> {
    public SermonSeriesService(ContentSupport support,SermonSeriesRepository repository,SermonSeriesMapper mapper) { super(support,repository,mapper,SermonSeries.class,"sermon-series",Set.of("id","createdAt","updatedAt","title","slug","description","startDate","endDate"),List.of("title","slug")); }
    protected SermonSeries fresh() { return new SermonSeries(); }
    protected Long version(SermonSeriesRequest request) { return request.version(); }
}
