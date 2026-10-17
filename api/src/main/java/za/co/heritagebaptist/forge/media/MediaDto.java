package za.co.heritagebaptist.forge.media;
import java.util.*;
public record MediaDto(UUID id,long version,String key,String originalName,String contentType,long size,String sha256,Integer width,Integer height,Integer durationSeconds,Map<String,Object> variants) {
    public static MediaDto of(MediaAsset e) { return new MediaDto(e.getId(),e.getVersion(),e.getStorageKey(),e.getOriginalName(),e.getContentType(),e.getSize(),e.getSha256(),e.getWidth(),e.getHeight(),e.getDuration(),e.getVariants()); }
}
