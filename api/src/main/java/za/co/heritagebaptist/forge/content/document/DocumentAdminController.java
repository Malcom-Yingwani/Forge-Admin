package za.co.heritagebaptist.forge.content.document;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/documents")
public class DocumentAdminController {
    private final DocumentService service;
    public DocumentAdminController(DocumentService service) { this.service=service; }
    @GetMapping public PageResponse<DocumentAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="title,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public DocumentAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<DocumentAdminDto> create(@Valid @RequestBody DocumentRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/documents/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public DocumentAdminDto update(@PathVariable UUID id,@Valid @RequestBody DocumentRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public DocumentAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public DocumentAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
    @PutMapping("/order") @ResponseStatus(HttpStatus.NO_CONTENT) public void order(@RequestBody List<UUID> ids) { service.order(ids); }
}
