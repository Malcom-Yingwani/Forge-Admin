package za.co.heritagebaptist.forge.content.page;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/pages")
public class PageAdminController {
    private final PageService service;
    public PageAdminController(PageService service) { this.service=service; }
    @GetMapping public PageResponse<PageAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="slug,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public PageAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<PageAdminDto> create(@Valid @RequestBody PageRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/pages/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public PageAdminDto update(@PathVariable UUID id,@Valid @RequestBody PageRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public PageAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public PageAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
}
