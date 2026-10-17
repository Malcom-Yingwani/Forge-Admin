package za.co.heritagebaptist.forge.content.servicetime;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/service-times")
public class ServiceTimeAdminController {
    private final ServiceTimeService service;
    public ServiceTimeAdminController(ServiceTimeService service) { this.service=service; }
    @GetMapping public PageResponse<ServiceTimeAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="name,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public ServiceTimeAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<ServiceTimeAdminDto> create(@Valid @RequestBody ServiceTimeRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/service-times/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public ServiceTimeAdminDto update(@PathVariable UUID id,@Valid @RequestBody ServiceTimeRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
