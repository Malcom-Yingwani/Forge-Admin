package za.co.heritagebaptist.forge.content.person;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/people")
public class PersonAdminController {
    private final PersonService service;
    public PersonAdminController(PersonService service) { this.service=service; }
    @GetMapping public PageResponse<PersonAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="name,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public PersonAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<PersonAdminDto> create(@Valid @RequestBody PersonRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/people/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public PersonAdminDto update(@PathVariable UUID id,@Valid @RequestBody PersonRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
    @PostMapping("/{id}/publish") public PersonAdminDto publish(@PathVariable UUID id) { return service.publish(id,true); }
    @PostMapping("/{id}/unpublish") public PersonAdminDto unpublish(@PathVariable UUID id) { return service.publish(id,false); }
    @PutMapping("/order") @ResponseStatus(HttpStatus.NO_CONTENT) public void order(@RequestBody List<UUID> ids) { service.order(ids); }
}
