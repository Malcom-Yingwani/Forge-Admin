package za.co.heritagebaptist.forge.media;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.time.*;
import java.util.*;
import org.apache.tika.Tika;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.heritagebaptist.forge.common.*;
import za.co.heritagebaptist.forge.content.admin.ContentSupport;
import za.co.heritagebaptist.forge.sync.SyncHook;
@Service
@Transactional(readOnly=true)
@PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
public class MediaService {
    private static final Map<String,String> EXTENSIONS=Map.of("audio/mpeg","mp3","audio/mp4","m4a","image/jpeg","jpg","image/png","png","image/webp","webp","application/pdf","pdf");
    private final MediaAssetRepository assets;
    private final StorageService storage;
    private final StorageCleanup cleanup;
    private final ImageVariants images;
    private final AuditService audit;
    private final ContentSupport support;
    private final ObjectProvider<SyncHook> hook;
    private final Tika detector=new Tika();
    public MediaService(MediaAssetRepository assets,StorageService storage,StorageCleanup cleanup,ImageVariants images,AuditService audit,ContentSupport support,ObjectProvider<SyncHook> hook) {
        this.assets=assets;this.storage=storage;this.cleanup=cleanup;this.images=images;this.audit=audit;this.support=support;this.hook=hook;
    }
    @Transactional(rollbackFor=Exception.class)
    public MediaDto upload(MultipartFile file) throws IOException {
        if(file.isEmpty()) throw new MediaExceptionHandler.Rejected(415,"Choose a supported media file");
        String mime;try(var in=new BufferedInputStream(file.getInputStream())) { mime=detector.detect(in); }
        String extension=EXTENSIONS.get(mime);if(extension==null) throw new MediaExceptionHandler.Rejected(415,"Unsupported file type");
        long limit=(mime.startsWith("audio/")?200L:mime.startsWith("image/")?10L:25L)*1024*1024;
        if(file.getSize()>limit) throw new MediaExceptionHandler.Rejected(413,"This media file exceeds its size limit");
        Path temporary=Files.createTempFile("forge-upload-","."+extension);List<String> stored=new ArrayList<>();
        try {
            MessageDigest digest=ImageVariants.sha256();long copied=0;
            try(var in=new DigestInputStream(file.getInputStream(),digest);var out=Files.newOutputStream(temporary)) {
                byte[] buffer=new byte[65536];int read;while((read=in.read(buffer))!=-1) { copied+=read;if(copied>limit) throw new MediaExceptionHandler.Rejected(413,"This media file exceeds its size limit");out.write(buffer,0,read); }
            }
            var asset=new MediaAsset();asset.setStorageKey(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM").format(LocalDate.now(ZoneId.of("Africa/Johannesburg")))+"/"+asset.getId()+"."+extension);
            String name=file.getOriginalFilename();if(name==null||name.isBlank()) name="upload."+extension;name=name.replace('\\','/');name=name.substring(name.lastIndexOf('/')+1).replaceAll("[\\p{Cntrl}]","_");
            asset.setOriginalName(name.length()>255?name.substring(0,255):name);asset.setContentType(mime);asset.setSize(copied);asset.setSha256(HexFormat.of().formatHex(digest.digest()));
            if(mime.startsWith("audio/")) {
                try { asset.setDuration(AudioFileIO.read(temporary.toFile()).getAudioHeader().getTrackLength()); }
                catch(Exception exception) { throw new MediaExceptionHandler.Rejected(415,"The audio file could not be read"); }
            }
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) { if(status!=STATUS_COMMITTED&&!stored.isEmpty()) cleanup.enqueueOrphans(stored); }
            });
            stored.add(asset.getStorageKey());try(var in=Files.newInputStream(temporary)) { storage.put(asset.getStorageKey(),in,copied,mime); }
            if(mime.startsWith("image/")) images.create(temporary,asset,storage,stored);
            assets.saveAndFlush(asset);audit.record(support.actor(),"UPLOAD","media",asset.getId(),Map.of("contentType",mime,"size",copied));hook.getObject().onMediaStored(asset);
            return MediaDto.of(asset);
        } finally { Files.deleteIfExists(temporary); }
    }
    public MediaAsset get(UUID id) { return assets.findById(id).orElseThrow(() -> new NotFoundException("Media not found")); }
    public InputStream open(UUID id) throws IOException { return storage.get(get(id).getStorageKey()); }
    public PageResponse<MediaDto> list(String type,String q,int page,int size) {
        if(page<0||page>100000||size<1||size>100) throw new MediaExceptionHandler.Rejected(400,"Invalid paging");
        String mime=type==null?null:switch(type) { case "audio" -> "audio/%";case "image","images" -> "image/%";case "pdf" -> "application/pdf";default -> EXTENSIONS.containsKey(type)?type:null; };
        if(type!=null&&mime==null) throw new MediaExceptionHandler.Rejected(400,"Invalid media type");
        var cb=support.em.getCriteriaBuilder();var query=cb.createQuery(MediaAsset.class);var root=query.from(MediaAsset.class);List<jakarta.persistence.criteria.Predicate> predicates=new ArrayList<>();
        if(mime!=null) predicates.add(cb.like(root.get("contentType"),mime));if(q!=null&&!q.isBlank()) predicates.add(cb.like(cb.lower(root.get("originalName")),"%"+q.toLowerCase(Locale.ROOT).replace("%","\\%").replace("_","\\_")+"%",'\\'));
        query.where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new)).orderBy(cb.desc(root.get("createdAt")),cb.asc(root.get("id")));
        var rows=support.em.createQuery(query).setFirstResult(page*size).setMaxResults(size).getResultList().stream().map(MediaDto::of).toList();
        var count=cb.createQuery(Long.class);var counted=count.from(MediaAsset.class);count.select(cb.count(counted));List<jakarta.persistence.criteria.Predicate> constraints=new ArrayList<>();
        if(mime!=null) constraints.add(cb.like(counted.get("contentType"),mime));if(q!=null&&!q.isBlank()) constraints.add(cb.like(cb.lower(counted.get("originalName")),"%"+q.toLowerCase(Locale.ROOT).replace("%","\\%").replace("_","\\_")+"%",'\\'));
        count.where(constraints.toArray(jakarta.persistence.criteria.Predicate[]::new));long total=support.em.createQuery(count).getSingleResult();return new PageResponse<>(rows,page,size,total,(int)Math.ceil((double)total/size));
    }
    @Transactional public void delete(UUID id) {
        var asset=get(id);List<String> keys=new ArrayList<>();keys.add(asset.getStorageKey());
        if(asset.getVariants()!=null) for(Object value:asset.getVariants().values()) if(value instanceof Map<?,?> variant&&variant.get("key") instanceof String key) keys.add(key);
        assets.delete(asset);assets.flush();cleanup.enqueue(keys);audit.record(support.actor(),"DELETE","media",id,Map.of("key",asset.getStorageKey()));
    }
}
