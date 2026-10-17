package za.co.heritagebaptist.forge.content.event;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/events")
public class EventAdminController {
    private final EventService service;
    public EventAdminController(EventService service) { this.service=service; }
    @GetMapping public PageResponse<EventAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="title,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public EventAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<EventAdminDto> create(@Valid @RequestBody EventRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/events/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public EventAdminDto update(@PathVariable UUID id,@Valid @RequestBody EventRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public EventAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public EventAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
}
