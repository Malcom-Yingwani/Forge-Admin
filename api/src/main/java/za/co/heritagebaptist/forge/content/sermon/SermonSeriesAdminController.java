package za.co.heritagebaptist.forge.content.sermon;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/sermon-series")
public class SermonSeriesAdminController {
    private final SermonSeriesService service;
    public SermonSeriesAdminController(SermonSeriesService service) { this.service=service; }
    @GetMapping public PageResponse<SermonSeriesAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="title,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public SermonSeriesAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<SermonSeriesAdminDto> create(@Valid @RequestBody SermonSeriesRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/sermon-series/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public SermonSeriesAdminDto update(@PathVariable UUID id,@Valid @RequestBody SermonSeriesRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
