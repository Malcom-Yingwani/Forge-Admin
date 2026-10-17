package za.co.heritagebaptist.forge.content.blog;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/blog-posts")
public class BlogPostAdminController {
    private final BlogPostService service;
    public BlogPostAdminController(BlogPostService service) { this.service=service; }
    @GetMapping public PageResponse<BlogPostAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="title,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public BlogPostAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<BlogPostAdminDto> create(@Valid @RequestBody BlogPostRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/blog-posts/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public BlogPostAdminDto update(@PathVariable UUID id,@Valid @RequestBody BlogPostRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public BlogPostAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public BlogPostAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
}
