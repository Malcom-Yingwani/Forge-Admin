package za.co.heritagebaptist.forge.content.growthgroup;
import java.util.*;
import java.time.*;
import jakarta.validation.constraints.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import za.co.heritagebaptist.forge.common.PageResponse;
@RestController
@RequestMapping("/api/admin/growth-groups")
public class GrowthGroupAdminController {
    private final GrowthGroupService service;
    public GrowthGroupAdminController(GrowthGroupService service) { this.service=service; }
    @GetMapping public PageResponse<GrowthGroupAdminDto> list(@RequestParam(required=false) String q,@RequestParam(defaultValue="name,asc") String sort,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) { return service.list(q,sort,page,size); }
    @GetMapping("/{id}") public GrowthGroupAdminDto get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<GrowthGroupAdminDto> create(@Valid @RequestBody GrowthGroupRequest request) { var dto=service.create(request);return ResponseEntity.created(java.net.URI.create("/api/admin/growth-groups/"+dto.id())).body(dto); }
    @PutMapping("/{id}") public GrowthGroupAdminDto update(@PathVariable UUID id,@Valid @RequestBody GrowthGroupRequest request) { return service.update(id,request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}
