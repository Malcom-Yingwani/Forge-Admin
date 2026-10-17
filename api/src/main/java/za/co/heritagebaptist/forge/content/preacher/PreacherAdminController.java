package za.co.heritagebaptist.forge.content.preacher;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/preachers")
public class PreacherAdminController {
    private final PreacherService service;
    public PreacherAdminController(PreacherService service) { this.service=service; }
    @GetMapping public PageResponse<PreacherAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="name,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public PreacherAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<PreacherAdminDto> create(@Valid @RequestBody PreacherRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/preachers/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public PreacherAdminDto update(@PathVariable UUID id,@Valid @RequestBody PreacherRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
