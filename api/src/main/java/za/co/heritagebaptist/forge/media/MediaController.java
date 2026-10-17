package za.co.heritagebaptist.forge.media;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/media")
public class MediaController {
    private final MediaService media;
    public MediaController(MediaService media) { this.media=media; }
    @PostMapping public ResponseEntity<MediaDto> upload(@RequestParam MultipartFile file) throws IOException { var dto=media.upload(file);return ResponseEntity.created(java.net.URI.create("/api/admin/media/"+dto.id())).body(dto); }
    @GetMapping public PageResponse<MediaDto> list(@RequestParam(required=false) String type,@RequestParam(required=false) String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return media.list(type,q,page,size); }
    @GetMapping("/{id}/file") public ResponseEntity<InputStreamResource> preview(@PathVariable UUID id) throws IOException {
        var asset=media.get(id);return ResponseEntity.ok().contentType(MediaType.parseMediaType(asset.getContentType())).contentLength(asset.getSize())
            .cacheControl(CacheControl.noStore()).header("X-Content-Type-Options","nosniff")
            .header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(asset.getOriginalName(),java.nio.charset.StandardCharsets.UTF_8).build().toString()).body(new InputStreamResource(media.open(id)));
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { media.delete(id); }
}
