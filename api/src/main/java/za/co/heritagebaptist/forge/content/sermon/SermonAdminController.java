package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/sermons")
public class SermonAdminController {
    private final SermonService service;
    public SermonAdminController(SermonService service) { this.service=service; }
    @GetMapping public PageResponse<SermonAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="title,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public SermonAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<SermonAdminDto> create(@Valid @RequestBody SermonRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/sermons/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public SermonAdminDto update(@PathVariable UUID id,@Valid @RequestBody SermonRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public SermonAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public SermonAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
}
